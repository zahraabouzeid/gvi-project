param(
    [string]$AppName = "GVIProject",
    [string]$AppVersion = "1.0.0",
    [string]$MainClass = "com.gvi.project.MainApp",
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

function Write-Step([string]$message) {
    Write-Host "[build-exe] $message" -ForegroundColor Cyan
}

if (-not (Test-Path ".\mvnw.cmd")) {
    throw "mvnw.cmd not found. Run this script from the project root."
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java is not available on PATH. Install JDK 21+ and try again."
}

$jpackageCommand = Get-Command jpackage -ErrorAction SilentlyContinue
if (-not $jpackageCommand) {
    throw "jpackage not found on PATH. Use a full JDK (21+) and ensure jpackage is available."
}

$runtimeImage = $env:JAVA_HOME
if (-not $runtimeImage -or -not (Test-Path $runtimeImage)) {
    $javaPath = (Get-Command java).Source
    $runtimeImage = Split-Path -Parent (Split-Path -Parent $javaPath)
}

if (-not (Test-Path $runtimeImage)) {
    throw "Could not determine runtime image. Set JAVA_HOME to your JDK path and try again."
}

$skipTestsArg = if ($SkipTests) { "-DskipTests" } else { "" }

Write-Step "Cleaning and packaging Maven project..."
& .\mvnw.cmd clean package $skipTestsArg
if ($LASTEXITCODE -ne 0) {
    Write-Warning "Clean package failed. This often happens when the previous EXE/app is still running and locks files in target/."
    Write-Step "Retrying without clean (package only)..."
    & .\mvnw.cmd package $skipTestsArg
    if ($LASTEXITCODE -ne 0) {
        throw "Maven package failed. Close running app instances and try again."
    }
}

Write-Step "Copying runtime dependencies into target\\libs..."
& .\mvnw.cmd dependency:copy-dependencies "-DincludeScope=runtime" "-DoutputDirectory=target/libs"
if ($LASTEXITCODE -ne 0) {
    throw "Failed to copy runtime dependencies."
}

$targetDir = Join-Path $scriptDir "target"
$inputDir = Join-Path $targetDir "jpackage-input"
$distDir = Join-Path $targetDir "dist"

if (Test-Path $inputDir) {
    Remove-Item $inputDir -Recurse -Force
}
if (Test-Path $distDir) {
    Remove-Item $distDir -Recurse -Force
}

New-Item -ItemType Directory -Path $inputDir | Out-Null
New-Item -ItemType Directory -Path $distDir | Out-Null

$mainJar = Get-ChildItem -Path $targetDir -Filter "*.jar" |
    Where-Object {
        $_.Name -notlike "original-*.jar" -and
        $_.Name -notlike "*-sources.jar" -and
        $_.Name -notlike "*-javadoc.jar"
    } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $mainJar) {
    throw "No main application jar found in target/."
}

Write-Step "Using main jar: $($mainJar.Name)"
Copy-Item $mainJar.FullName $inputDir -Force

$libsDir = Join-Path $targetDir "libs"
if (Test-Path $libsDir) {
    Copy-Item (Join-Path $libsDir "*.jar") $inputDir -Force
}

Write-Step "Creating self-contained app image..."
& jpackage `
    --type app-image `
    --name $AppName `
    --input $inputDir `
    --main-jar $mainJar.Name `
    --main-class $MainClass `
    --dest $distDir `
    --app-version $AppVersion `
    --runtime-image $runtimeImage `
    --java-options "--enable-native-access=ALL-UNNAMED" `
    --java-options "--enable-native-access=javafx.graphics"

if ($LASTEXITCODE -ne 0) {
    throw "jpackage app-image creation failed."
}

Write-Step "Creating Windows .exe installer..."
$hasWixLight = [bool](Get-Command light.exe -ErrorAction SilentlyContinue)
$hasWixCandle = [bool](Get-Command candle.exe -ErrorAction SilentlyContinue)

$exeSucceeded = $false
if ($hasWixLight -and $hasWixCandle) {
    try {
        & jpackage `
            --type exe `
            --name $AppName `
            --app-image (Join-Path $distDir $AppName) `
            --dest $distDir `
            --app-version $AppVersion

        if ($LASTEXITCODE -eq 0) {
            $exeSucceeded = $true
        }
    }
    catch {
        $exeSucceeded = $false
    }
}

if ($exeSucceeded) {
    Write-Host "`nSuccess: EXE installer generated in $distDir" -ForegroundColor Green
}
else {
    Write-Warning "EXE installer was not generated."
    if (-not $hasWixLight -or -not $hasWixCandle) {
        Write-Warning "WiX Toolset v3 (light.exe/candle.exe) was not found on PATH."
        Write-Host "Install with: winget install --id WiXToolset.WiXToolset -e --accept-package-agreements --accept-source-agreements" -ForegroundColor Yellow
        Write-Host "Then restart your terminal and run this script again." -ForegroundColor Yellow
    }
    Write-Host "App image is available at: $(Join-Path $distDir $AppName)" -ForegroundColor Yellow
    Write-Host "You can run it directly from: $(Join-Path $distDir "$AppName\$AppName.exe")" -ForegroundColor Yellow
}
