# Gamification of Learning

## Thema: SQL und Datenbankmodellierung

Teammitglieder: Zahraa, Masuma, Rebecca, Amélie, Dominik, Sascha

<a href="https://gamificationoflearning.atlassian.net/jira/software/projects/SCRUM/boards/1?jql=" target="_blank">Storyboard</a>

## 1. Meeting

Entscheidungen:

- Fragen und Antworten in <a href="https://docs.google.com/spreadsheets/d/1PzCFhDtFhrNBi1LWgHcfVVqyidXH1lMaXFp0_CgTHlE/edit?gid=0#gid=0" target="_blank">Google Doc</a>
  - Keine Dopplungen
  - Echtzeit Zusammenarbeit
  - Korrektur durch Team
  - Export Möglichkeiten für DB Import
- Keine Gameengine
- Java mit Maven

## 2. Zeitplan

<img width="1155" height="700" alt="image" src="https://github.com/user-attachments/assets/d4297df6-a121-4f33-883e-8f276fd0d2cd" />

## 3. Regeln

- Eindeutige und beschreibende Commit Messages
- Dokumentieren des geschriebenen Codes
- Code wird auf nur auf Englisch geschrieben Kommentare und Doku auf Deutsch

## 4. Build & Run

**Kompilieren:**

```powershell
mvn -q -DskipTests javafx:run
```

**Ausführen:**

```powershell
mvn -q javafx:run
```

**Build und Run zusammen:**

```powershell
mvn -q -DskipTests clean javafx:run
```

**Alternative Kompilier/Ausführ variante:**    
Wenn man mit Debugger arbeiten möchte ist man leider hierzu gezwungen, anderweitig läuft die complierte version über eine separate Instance zur IDE.  
In IntelliJ, rechts in der Seitenleiste m -> gviProject -> plugins -> javafx -> javafx:run, ausführen mit doppelclick.  
Alternativ JavaFx 21.0.2 <a href="https://gluonhq.com/products/javafx/" target="_blank">downloaden</a>, ihr müsst archivierte Versionen anzeigen lassen um die zu finden, und in entpacken an einer von euch gewünschten stelle.  
<img width="1224" height="667" alt="image" src="https://github.com/user-attachments/assets/87ce7d17-4782-4eac-9747-79be57b1873e" />  
Anschließend ein Run conifig anlegen, über modify options -> add VM options, die VM options anzeigen lassen und folgende Argumente eintragen

```powershell
--module-path "C:\SDK\java\javaFx\javafx-sdk-21.0.2\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=ALL-UNNAMED --enable-native-access=javafx.graphics
```
