param(
    [string]$ProjectRoot = ".",
    [string]$AppName = "",
    [switch]$InstallMissing,
    [switch]$SkipTests,
    [switch]$PersistUserPath,
    [switch]$KeepPortableFolder
)

$ErrorActionPreference = "Stop"

function Write-Info {
    param([string]$Message)
    Write-Host "[build-exe] $Message"
}

function Throw-IfNotWindows {
    $platform = [System.Environment]::OSVersion.Platform
    $isWindowsPlatform = ($env:OS -eq 'Windows_NT') -or ($platform -eq [System.PlatformID]::Win32NT)

    if (-not $isWindowsPlatform) {
        throw "Dieses Skript unterstützt nur Windows."
    }
}

function Test-Administrator {
    $currentIdentity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentIdentity)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Set-SessionPathFront {
    param([Parameter(Mandatory = $true)][string]$PathToAdd)

    if ([string]::IsNullOrWhiteSpace($PathToAdd)) { return }
    if (-not (Test-Path -LiteralPath $PathToAdd)) { return }

    $existingParts = @()
    if (-not [string]::IsNullOrWhiteSpace($env:Path)) {
        $existingParts = $env:Path -split ';'
    }

    foreach ($part in $existingParts) {
        if ($part -eq $PathToAdd) {
            return
        }
    }

    if ([string]::IsNullOrWhiteSpace($env:Path)) {
        $env:Path = $PathToAdd
    }
    else {
        $env:Path = "$PathToAdd;$env:Path"
    }

    Write-Info "Added to session PATH: $PathToAdd"
}

function Set-UserEnvVarIfRequested {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$Value,
        [switch]$PersistUser
    )

    Set-Item -Path "Env:$Name" -Value $Value

    if ($PersistUser) {
        [Environment]::SetEnvironmentVariable($Name, $Value, "User")
        Write-Info "Persisted user environment variable: $Name=$Value"
    }
}

function Refresh-ProcessEnvironment {
    $machinePath = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $userPath = [Environment]::GetEnvironmentVariable("Path", "User")

    $pathParts = New-Object System.Collections.Generic.List[string]

    if (-not [string]::IsNullOrWhiteSpace($machinePath)) {
        foreach ($p in ($machinePath -split ';')) {
            if (-not [string]::IsNullOrWhiteSpace($p)) {
                [void]$pathParts.Add($p)
            }
        }
    }

    if (-not [string]::IsNullOrWhiteSpace($userPath)) {
        foreach ($p in ($userPath -split ';')) {
            if (-not [string]::IsNullOrWhiteSpace($p)) {
                [void]$pathParts.Add($p)
            }
        }
    }

    if ($pathParts.Count -gt 0) {
        $env:Path = (($pathParts | Select-Object -Unique) -join ';')
    }

    $userJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "User")
    $machineJavaHome = [Environment]::GetEnvironmentVariable("JAVA_HOME", "Machine")

    if (-not [string]::IsNullOrWhiteSpace($userJavaHome)) {
        $env:JAVA_HOME = $userJavaHome
    }
    elseif (-not [string]::IsNullOrWhiteSpace($machineJavaHome)) {
        $env:JAVA_HOME = $machineJavaHome
    }
}

function Install-ChocolateyIfNeeded {
    if (Get-Command choco -ErrorAction SilentlyContinue) {
        return
    }

    if (-not $InstallMissing) {
        throw "Chocolatey wurde nicht gefunden. Starte mit -InstallMissing oder installiere Chocolatey manuell."
    }

    if (-not (Test-Administrator)) {
        throw "Chocolatey fehlt. Für die automatische Installation bitte PowerShell als Administrator starten."
    }

    Write-Info "Installing Chocolatey..."
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    Refresh-ProcessEnvironment
}

function Install-ChocoPackage {
    param([Parameter(Mandatory = $true)][string]$PackageName)

    Install-ChocolateyIfNeeded

    Write-Info "Installing package via Chocolatey: $PackageName"
    & choco install $PackageName -y --no-progress
    if ($LASTEXITCODE -ne 0) {
        throw "Chocolatey installation failed for package: $PackageName"
    }

    if (Get-Command refreshenv -ErrorAction SilentlyContinue) {
        try {
            refreshenv | Out-Null
        }
        catch {
        }
    }

    Refresh-ProcessEnvironment
}

function Get-RegistryJdkHomes {
    $results = New-Object System.Collections.Generic.List[string]

    $roots = @(
        "HKLM:\SOFTWARE\JavaSoft\JDK",
        "HKLM:\SOFTWARE\WOW6432Node\JavaSoft\JDK",
        "HKLM:\SOFTWARE\Eclipse Adoptium",
        "HKLM:\SOFTWARE\WOW6432Node\Eclipse Adoptium",
        "HKLM:\SOFTWARE\Adoptium",
        "HKLM:\SOFTWARE\WOW6432Node\Adoptium",
        "HKLM:\SOFTWARE\Amazon Corretto",
        "HKLM:\SOFTWARE\WOW6432Node\Amazon Corretto"
    )

    foreach ($root in $roots) {
        if (-not (Test-Path -LiteralPath $root)) {
            continue
        }

        try {
            foreach ($child in (Get-ChildItem -LiteralPath $root -ErrorAction SilentlyContinue)) {
                try {
                    $props = Get-ItemProperty -LiteralPath $child.PSPath -ErrorAction SilentlyContinue
                    foreach ($propName in @("Path", "JavaHome", "InstallationPath", "Home")) {
                        $value = $props.$propName
                        if (-not [string]::IsNullOrWhiteSpace($value) -and (Test-Path -LiteralPath $value)) {
                            [void]$results.Add($value)
                        }
                    }
                }
                catch {
                }
            }
        }
        catch {
        }
    }

    return $results | Select-Object -Unique
}

function Get-JavaMajorVersion {
    param([Parameter(Mandatory = $true)][string]$JavaExe)

    try {
        $output = & $JavaExe -version 2>&1 | Out-String
    }
    catch {
        return $null
    }

    foreach ($pattern in @(
        'version\s+"(?<version>\d+)(\.\d+)?',
        'openjdk version\s+"(?<version>\d+)(\.\d+)?',
        'openjdk\s+(?<version>\d+)(\.\d+)?',
        'java\s+(?<version>\d+)(\.\d+)?'
    )) {
        $versionMatch = [regex]::Match($output, $pattern)
        if ($versionMatch.Success) {
            return [int]$versionMatch.Groups['version'].Value
        }
    }

    return $null
}

function Get-JPackageMajorVersion {
    param([Parameter(Mandatory = $true)][string]$JpackageExe)

    try {
        $output = & $JpackageExe --version 2>&1 | Out-String
    }
    catch {
        return $null
    }

    $versionMatch = [regex]::Match($output, '(?<version>\d+)(\.\d+)?')
    if ($versionMatch.Success) {
        return [int]$versionMatch.Groups['version'].Value
    }

    return $null
}

function Get-JdkCandidates {
    $candidates = New-Object System.Collections.Generic.List[string]

    if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
        [void]$candidates.Add($env:JAVA_HOME)
    }

    foreach ($candidateVar in @('JDK_HOME', 'JAVA21_HOME')) {
        $value = [Environment]::GetEnvironmentVariable($candidateVar, 'Process')
        if (-not [string]::IsNullOrWhiteSpace($value)) {
            [void]$candidates.Add($value)
        }
    }

    foreach ($cmdName in @('jpackage', 'java')) {
        $cmd = Get-Command $cmdName -ErrorAction SilentlyContinue
        if ($cmd) {
            try {
                $binDir = Split-Path -Parent $cmd.Source
                $homeDir = Split-Path -Parent $binDir
                [void]$candidates.Add($homeDir)
            }
            catch {}
        }
    }

    foreach ($jdkHome in Get-RegistryJdkHomes) {
        [void]$candidates.Add($jdkHome)
    }

    $roots = @(
        'C:\Program Files\Eclipse Adoptium',
        'C:\Program Files\Adoptium',
        'C:\Program Files\Temurin',
        'C:\Program Files\Java',
        'C:\Program Files\Microsoft',
        'C:\hostedtoolcache\windows',
        'C:\Program Files\Amazon Corretto'
    )

    foreach ($root in $roots) {
        if (-not (Test-Path -LiteralPath $root)) {
            continue
        }

        try {
            $entries = New-Object System.Collections.Generic.List[System.IO.DirectoryInfo]

            foreach ($item in (Get-ChildItem -LiteralPath $root -Directory -ErrorAction SilentlyContinue)) {
                [void]$entries.Add($item)
            }

            foreach ($item in (Get-ChildItem -LiteralPath $root -Directory -Recurse -ErrorAction SilentlyContinue)) {
                [void]$entries.Add($item)
            }

            $entries |
                    Sort-Object FullName -Unique |
                    Where-Object {
                        (Test-Path -LiteralPath (Join-Path $_.FullName 'bin\java.exe')) -and
                                (Test-Path -LiteralPath (Join-Path $_.FullName 'bin\jpackage.exe'))
                    } |
                    ForEach-Object {
                        [void]$candidates.Add($_.FullName)
                    }
        }
        catch {}
    }

    return $candidates |
            Where-Object { -not [string]::IsNullOrWhiteSpace($_) } |
            ForEach-Object { $_.TrimEnd('\\') } |
            Select-Object -Unique
}

function Find-Jdk {
    $rejectedCandidates = New-Object System.Collections.Generic.List[string]

    foreach ($candidate in Get-JdkCandidates) {
        $javaExe = Join-Path $candidate 'bin\java.exe'
        $jpackageExe = Join-Path $candidate 'bin\jpackage.exe'

        if (-not (Test-Path -LiteralPath $javaExe) -or -not (Test-Path -LiteralPath $jpackageExe)) {
            continue
        }

        $major = Get-JavaMajorVersion -JavaExe $javaExe
        if ($null -eq $major) {
            $major = Get-JPackageMajorVersion -JpackageExe $jpackageExe
        }

        if ($null -ne $major -and $major -ge 21) {
            return [pscustomobject]@{
                Home         = $candidate
                JavaExe      = $javaExe
                JpackageExe  = $jpackageExe
                MajorVersion = $major
            }
        }

        [void]$rejectedCandidates.Add($candidate)
    }

    if ($rejectedCandidates.Count -gt 0) {
        Write-Info ("Detected JDK candidate directories but none met the requirements: " + (($rejectedCandidates | Select-Object -Unique) -join "; "))
    }

    return $null
}

function Install-Jdk {
    $jdk = Find-Jdk
    if ($jdk) {
        return $jdk
    }

    if (-not $InstallMissing) {
        throw "No suitable JDK 21+ with jpackage was found. Install one manually or re-run with -InstallMissing."
    }

    foreach ($packageName in @('temurin21', 'temurin')) {
        try {
            Install-ChocoPackage -PackageName $packageName
        }
        catch {
            Write-Info "Chocolatey package '$packageName' could not be installed automatically: $($_.Exception.Message)"
        }

        if (Get-Command refreshenv -ErrorAction SilentlyContinue) {
            try { refreshenv | Out-Null } catch {}
        }

        Refresh-ProcessEnvironment
        $jdk = Find-Jdk
        if (-not $jdk) {
            Start-Sleep -Seconds 2
            Refresh-ProcessEnvironment
            $jdk = Find-Jdk
        }

        if ($jdk) {
            return $jdk
        }
    }

    $detectedCandidates = @(Get-JdkCandidates)
    if ($detectedCandidates.Count -gt 0) {
        Write-Info ('Detected JDK candidate directories but none met the requirements: ' + ($detectedCandidates -join '; '))
    }

    throw "A JDK package was installed, but no suitable JDK 21+ with jpackage could be detected afterwards. Check where Temurin was installed, or set JAVA_HOME manually to the JDK root and rerun the script."
}

function Find-7ZipConsole {
    $cmd = Get-Command 7z.exe -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }

    foreach ($path in @(
        "C:\Program Files\7-Zip\7z.exe",
        "C:\Program Files (x86)\7-Zip\7z.exe"
    )) {
        if (Test-Path -LiteralPath $path) { return $path }
    }

    return $null
}

function Find-7ZipSfxModule {
    foreach ($path in @(
        "C:\Program Files\7-Zip\7z.sfx",
        "C:\Program Files (x86)\7-Zip\7z.sfx"
    )) {
        if (Test-Path -LiteralPath $path) { return $path }
    }

    return $null
}

function Install-7ZipIfNeeded {
    $sevenZipExe = Find-7ZipConsole
    if ($sevenZipExe) { return $sevenZipExe }

    if (-not $InstallMissing) {
        throw "7-Zip wurde nicht gefunden. Für die Ein-Datei-EXE ist 7-Zip erforderlich. Starte mit -InstallMissing oder installiere 7-Zip manuell."
    }

    Install-ChocoPackage -PackageName "7zip"

    $sevenZipExe = Find-7ZipConsole
    if (-not $sevenZipExe) {
        throw "7-Zip wurde installiert, aber 7z.exe wurde nicht gefunden."
    }

    return $sevenZipExe
}

function Create-SfxConfig {
    param(
        [Parameter(Mandatory = $true)][string]$ConfigPath,
        [Parameter(Mandatory = $true)][string]$ExecutableRelativePath,
        [Parameter(Mandatory = $true)][string]$Title
    )

    @"
;!@Install@!UTF-8!
Title="$Title"
RunProgram="$ExecutableRelativePath"
;!@InstallEnd@!
"@ | Set-Content -Path $ConfigPath -Encoding UTF8
}

function Build-SingleFilePortableExe {
    param(
        [Parameter(Mandatory = $true)][string]$PortableRoot,
        [Parameter(Mandatory = $true)][string]$OutputExe,
        [Parameter(Mandatory = $true)][string]$AppExeName
    )

    $sevenZipExe = Install-7ZipIfNeeded
    $sevenZipSfx = Find-7ZipSfxModule

    if (-not $sevenZipSfx) {
        throw "7z.sfx wurde nicht gefunden. Installiere die normale 7-Zip Desktop-Version."
    }

    $workDir = Join-Path ([System.IO.Path]::GetTempPath()) ("gvi-sfx-" + [Guid]::NewGuid().ToString("N"))
    New-Item -ItemType Directory -Path $workDir -Force | Out-Null

    try {
        $stagingDir = Join-Path $workDir "payload"
        New-Item -ItemType Directory -Path $stagingDir -Force | Out-Null

        $portableFolderName = Split-Path $PortableRoot -Leaf
        $portableCopyTarget = Join-Path $stagingDir $portableFolderName
        Copy-Item -LiteralPath $PortableRoot -Destination $portableCopyTarget -Recurse -Force

        $archivePath = Join-Path $workDir "payload.7z"
        $configPath = Join-Path $workDir "config.txt"
        $runProgram = "$portableFolderName\$AppExeName"

        Create-SfxConfig -ConfigPath $configPath -ExecutableRelativePath $runProgram -Title $portableFolderName

        Push-Location $stagingDir
        try {
            & $sevenZipExe a -t7z $archivePath * -mx=9 | Out-Null
            if ($LASTEXITCODE -ne 0) {
                throw "7-Zip Archiv-Erstellung fehlgeschlagen."
            }
        }
        finally {
            Pop-Location
        }

        $outputDir = Split-Path $OutputExe -Parent
        if (-not (Test-Path -LiteralPath $outputDir)) {
            New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
        }

        $sfxBytes = [System.IO.File]::ReadAllBytes($sevenZipSfx)
        $cfgBytes = [System.Text.Encoding]::UTF8.GetBytes((Get-Content -LiteralPath $configPath -Raw))
        $archiveBytes = [System.IO.File]::ReadAllBytes($archivePath)

        $fs = [System.IO.File]::Open($OutputExe, [System.IO.FileMode]::Create, [System.IO.FileAccess]::Write)
        try {
            $fs.Write($sfxBytes, 0, $sfxBytes.Length)
            $fs.Write($cfgBytes, 0, $cfgBytes.Length)
            $fs.Write($archiveBytes, 0, $archiveBytes.Length)
        }
        finally {
            $fs.Dispose()
        }

        Write-Info "Single-file portable EXE created: $OutputExe"
    }
    finally {
        Remove-Item -LiteralPath $workDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

function Build-PortableZip {
    param(
        [Parameter(Mandatory = $true)][string]$PortableRoot,
        [Parameter(Mandatory = $true)][string]$OutputZip
    )

    $outputDir = Split-Path $OutputZip -Parent
    if (-not (Test-Path -LiteralPath $outputDir)) {
        New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    }

    if (Test-Path -LiteralPath $OutputZip) {
        Remove-Item -LiteralPath $OutputZip -Force
    }

    Compress-Archive -Path $PortableRoot -DestinationPath $OutputZip -CompressionLevel Optimal
    Write-Info "Portable ZIP created: $OutputZip"
}

function Get-MavenCommand {
    $wrapper = Join-Path $ProjectRoot "mvnw.cmd"
    if (Test-Path -LiteralPath $wrapper) {
        return $wrapper
    }

    $mvn = Get-Command mvnw.cmd -ErrorAction SilentlyContinue
    if ($mvn) { return $mvn.Source }

    $mvn = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if (-not $mvn) { $mvn = Get-Command mvn -ErrorAction SilentlyContinue }
    if ($mvn) { return $mvn.Source }

    if (-not $InstallMissing) {
        throw "Weder mvnw.cmd noch Maven wurden gefunden."
    }

    Install-ChocoPackage -PackageName "maven"

    $mvn = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if (-not $mvn) { $mvn = Get-Command mvn -ErrorAction SilentlyContinue }
    if ($mvn) { return $mvn.Source }

    throw "Maven wurde nach der Installation nicht gefunden."
}

function Invoke-External {
    param(
        [Parameter(Mandatory = $true)][string[]]$Command,
        [string]$FailureMessage = "Externer Befehl fehlgeschlagen."
    )

    if (-not $Command -or $Command.Count -eq 0) {
        throw "Invoke-External received no command."
    }

    $exe = $Command[0]
    $args = @()
    if ($Command.Count -gt 1) {
        $args = $Command[1..($Command.Count - 1)]
    }

    & $exe @args
    if ($LASTEXITCODE -ne 0) {
        throw $FailureMessage
    }
}

function Resolve-PomValue {
    param(
        [string]$Value,
        [hashtable]$Properties
    )

    if ([string]::IsNullOrWhiteSpace($Value)) { return $Value }

    $resolved = $Value
    $safety = 0

    while ($safety -lt 10) {
        $matchObj = [regex]::Match($resolved, '\$\{([^}]+)\}')
        if (-not $matchObj.Success) { break }

        $key = $matchObj.Groups[1].Value
        if (-not $Properties.ContainsKey($key)) { break }

        $resolved = $resolved.Replace('${' + $key + '}', [string]$Properties[$key])
        $safety++
    }

    return $resolved
}

function Read-PomMetadata {
    $pomPath = Join-Path $ProjectRoot "pom.xml"
    if (-not (Test-Path -LiteralPath $pomPath)) {
        throw "pom.xml wurde nicht gefunden: $pomPath"
    }

    [xml]$pom = Get-Content -LiteralPath $pomPath
    $project = $pom.project

    $properties = @{}
    if ($project.properties) {
        foreach ($node in $project.properties.ChildNodes) {
            if ($node.NodeType -eq [System.Xml.XmlNodeType]::Element) {
                $properties[$node.Name] = $node.InnerText
            }
        }
    }

    $artifactId = $project.artifactId
    $version = $project.version
    $name = $project.name
    $description = $project.description

    if ([string]::IsNullOrWhiteSpace($version) -and $project.parent) {
        $version = $project.parent.version
    }

    $mainClass = $null
    if ($properties.ContainsKey("main.class")) {
        $mainClass = $properties["main.class"]
    }
    elseif ($properties.ContainsKey("mainClass")) {
        $mainClass = $properties["mainClass"]
    }

    if ([string]::IsNullOrWhiteSpace($mainClass)) {
        try {
            $plugins = $project.build.plugins.plugin
            foreach ($plugin in $plugins) {
                if ($plugin.configuration) {
                    if ($plugin.configuration.mainClass) {
                        $mainClass = $plugin.configuration.mainClass
                        break
                    }
                    if ($plugin.configuration.mainclass) {
                        $mainClass = $plugin.configuration.mainclass
                        break
                    }
                }
            }
        }
        catch {}
    }

    $artifactId = Resolve-PomValue -Value $artifactId -Properties $properties
    $version = Resolve-PomValue -Value $version -Properties $properties
    $name = Resolve-PomValue -Value $name -Properties $properties
    $description = Resolve-PomValue -Value $description -Properties $properties
    $mainClass = Resolve-PomValue -Value $mainClass -Properties $properties

    if ([string]::IsNullOrWhiteSpace($name)) { $name = $artifactId }

    return [pscustomobject]@{
        ArtifactId  = $artifactId
        Version     = $version
        Name        = $name
        Description = $description
        MainClass   = $mainClass
    }
}

function Copy-PortablePayload {
    param(
        [Parameter(Mandatory = $true)][string]$PortableRoot,
        [Parameter(Mandatory = $true)][string]$ProjectRoot
    )

    $appDir = Join-Path $PortableRoot "app"
    $dataDir = Join-Path $appDir "data"
    New-Item -ItemType Directory -Path $dataDir -Force | Out-Null

    foreach ($file in @(
        "AllQuestions_clear_themes.db",
        "quizdb.db",
        "application.yaml",
        "application.yml"
    )) {
        $sourcePath = Join-Path $ProjectRoot $file
        if (Test-Path -LiteralPath $sourcePath) {
            Copy-Item -LiteralPath $sourcePath -Destination $dataDir -Force
            Write-Info "Copied payload file: $file"
        }
    }

    $resourceRoot = Join-Path $ProjectRoot "src\main\resources"
    if (Test-Path -LiteralPath $resourceRoot) {
        foreach ($dir in (Get-ChildItem -LiteralPath $resourceRoot -Directory -ErrorAction SilentlyContinue)) {
            $targetPath = Join-Path $dataDir $dir.Name
            Copy-Item -LiteralPath $dir.FullName -Destination $targetPath -Recurse -Force
            Write-Info "Copied resource directory: $($dir.Name)"
        }
    }
}


Throw-IfNotWindows

$ProjectRoot = (Resolve-Path $ProjectRoot).Path
$metadata = Read-PomMetadata

if ([string]::IsNullOrWhiteSpace($AppName)) {
    $AppName = $metadata.Name
}

if ([string]::IsNullOrWhiteSpace($metadata.ArtifactId)) {
    throw "artifactId konnte nicht aus der pom.xml gelesen werden."
}

if ([string]::IsNullOrWhiteSpace($metadata.Version)) {
    throw "version konnte nicht aus der pom.xml gelesen werden."
}

if ([string]::IsNullOrWhiteSpace($metadata.MainClass)) {
    throw "Keine Main-Class in der pom.xml gefunden. Setze z. B. <main.class>com.gvi.project.Launcher</main.class>."
}

Write-Info "Project root: $ProjectRoot"
Write-Info "Application name: $AppName"
Write-Info "Artifact ID: $($metadata.ArtifactId)"
Write-Info "Application version: $($metadata.Version)"
Write-Info "Main class: $($metadata.MainClass)"

$jdk = Install-Jdk
$jdkHome = $jdk.Home
$jpackageExe = $jdk.JpackageExe

Set-UserEnvVarIfRequested -Name "JAVA_HOME" -Value $jdkHome -PersistUser:$PersistUserPath
Set-SessionPathFront -PathToAdd (Join-Path $jdkHome "bin")
Write-Info "Using JAVA_HOME: $jdkHome"

$mavenCommand = Get-MavenCommand
Write-Info "Using Maven command: $mavenCommand"

$targetDir = Join-Path $ProjectRoot "target"
$libsDir = Join-Path $targetDir "libs"
$distDir = Join-Path $ProjectRoot "dist"
$portableRoot = Join-Path $distDir $AppName
$singleFileExe = Join-Path $distDir ($AppName + ".exe")
$outputZip = Join-Path $distDir ($AppName + ".zip")

$buildArgs = @("clean", "package")
if ($SkipTests) {
    $buildArgs += "-DskipTests"
}

Write-Info "Cleaning and packaging Maven project..."
try {
    Invoke-External -Command (@($mavenCommand) + $buildArgs) -FailureMessage "Maven package failed."
}
catch {
    Write-Info "Clean package failed. Retrying package without clean in case files are locked by a running app."
    $retryArgs = @("package")
    if ($SkipTests) {
        $retryArgs += "-DskipTests"
    }
    Invoke-External -Command (@($mavenCommand) + $retryArgs) -FailureMessage "Maven package failed. Close running app instances and try again."
}

New-Item -ItemType Directory -Path $libsDir -Force | Out-Null

Write-Info "Copying runtime dependencies into target\libs..."
$copyDepsArgs = @(
    "dependency:copy-dependencies",
    "-DincludeScope=runtime",
    "-DoutputDirectory=$libsDir"
)
Invoke-External -Command (@($mavenCommand) + $copyDepsArgs) -FailureMessage "Maven dependency copy failed."

$jarCandidates = Get-ChildItem -LiteralPath $targetDir -Filter "*.jar" -File | Where-Object {
    $_.Name -notlike "*sources*" -and $_.Name -notlike "*javadoc*"
}

$mainJar = $jarCandidates | Where-Object {
    $_.Name -eq "$($metadata.ArtifactId)-$($metadata.Version).jar"
} | Select-Object -First 1

if (-not $mainJar) {
    $mainJar = $jarCandidates | Where-Object {
        $_.Name -like "$($metadata.ArtifactId)-$($metadata.Version)*.jar"
    } | Select-Object -First 1
}

if (-not $mainJar) {
    $mainJar = $jarCandidates | Select-Object -First 1
}

if (-not $mainJar) {
    throw "Keine Haupt-JAR in target gefunden."
}

Write-Info "Using main JAR: $($mainJar.Name)"

if (Test-Path -LiteralPath $portableRoot) {
    try {
        Remove-Item -LiteralPath $portableRoot -Recurse -Force -ErrorAction Stop
    }
    catch {
        Write-Info "Portable output folder could not be fully cleaned. Continuing."
    }
}

if (Test-Path -LiteralPath $singleFileExe) {
    try {
        Remove-Item -LiteralPath $singleFileExe -Force -ErrorAction Stop
    }
    catch {
        Write-Info "Existing single-file EXE could not be removed before rebuild."
    }
}

if (Test-Path -LiteralPath $outputZip) {
    try {
        Remove-Item -LiteralPath $outputZip -Force -ErrorAction Stop
    }
    catch {
        Write-Info "Existing ZIP could not be removed before rebuild."
    }
}

New-Item -ItemType Directory -Path $distDir -Force | Out-Null

$jpackageInputDir = Join-Path $targetDir "jpackage-input"
if (Test-Path -LiteralPath $jpackageInputDir) {
    Remove-Item -LiteralPath $jpackageInputDir -Recurse -Force -ErrorAction SilentlyContinue
}
New-Item -ItemType Directory -Path $jpackageInputDir -Force | Out-Null
Copy-Item -LiteralPath $mainJar.FullName -Destination $jpackageInputDir -Force
if (Test-Path -LiteralPath $libsDir) {
    Copy-Item -LiteralPath $libsDir -Destination (Join-Path $jpackageInputDir "libs") -Recurse -Force
}
Write-Info "Prepared jpackage input: main JAR + libs only."

$jpackageArgs = @(
    "--type", "app-image",
    "--dest", $distDir,
    "--name", $AppName,
    "--input", $jpackageInputDir,
    "--main-jar", $mainJar.Name,
    "--main-class", $metadata.MainClass,
    "--java-options", "--enable-native-access=ALL-UNNAMED"
)

$iconPath = Join-Path $ProjectRoot "icon.ico"
if (Test-Path -LiteralPath $iconPath) {
    $jpackageArgs += @("--icon", $iconPath)
    Write-Info "Using icon: $iconPath"
}

if (-not [string]::IsNullOrWhiteSpace($metadata.Version)) {
    $jpackageArgs += @("--app-version", $metadata.Version)
}

Write-Info "Creating portable app-image..."
Invoke-External -Command (@($jpackageExe) + $jpackageArgs) -FailureMessage "jpackage app-image failed."

$appExePath = Join-Path $portableRoot ($AppName + ".exe")
if (-not (Test-Path -LiteralPath $appExePath)) {
    throw "Portable launcher not found after app-image build: $appExePath"
}

Copy-PortablePayload -PortableRoot $portableRoot -ProjectRoot $ProjectRoot
Write-Info "Portable app created temporarily: $appExePath"

Build-PortableZip -PortableRoot $portableRoot -OutputZip $outputZip

try {
    Build-SingleFilePortableExe -PortableRoot $portableRoot -OutputExe $singleFileExe -AppExeName ($AppName + ".exe")
}
catch {
    Write-Info "Single-file EXE konnte nicht erstellt werden (7-Zip fehlt oder blockiert): $($_.Exception.Message)"
    Write-Info "ZIP wurde trotzdem erstellt."
}

if (-not $KeepPortableFolder) {
    Write-Info "Removing temporary portable folder..."
    Remove-Item -LiteralPath $portableRoot -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Info "Cleaning up target directory..."
Remove-Item -LiteralPath $targetDir -Recurse -Force -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "[build-exe] Build finished."
Write-Host "Portable ZIP: $outputZip"
if (Test-Path -LiteralPath $singleFileExe) {
    Write-Host "Single-file EXE: $singleFileExe"
}
if ($KeepPortableFolder) {
    Write-Host "Temporary portable folder kept: $portableRoot"
}

exit 0
