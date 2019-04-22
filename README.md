# "*Mobile Synchronisation*"

## Aufgabenstellung
Die detaillierte [Aufgabenstellung](TASK.md) beschreibt die notwendigen Schritte zur Realisierung.

## Implementierung

Für die Synchronisation und Persistierung der Tasks wurde  Firebase verwendet. Die Funktionen, die davon für das Projekt verwendet wurden sind "Authentication" für Login/Register und "Firestore" als Datenbank. Firebase kümmert sich dabei selbst darum, dass wenn neue Daten in der Datenbank sind, die App das mitbekommt. Was dann genau mit den neuen Daten gemacht werden soll, muss durch den Programmierer bestimmt werden. (Mehr dazu später)  

In der [Firebase Dokumentation](https://firebase.google.com/docs/android/setup) sind alle Komponenten beschrieben und wie man diese in ein Android Studio Projekt einfügen kann, beziehungsweise welche verschiedenen Methoden für verschiedenste Anforderungen vorhanden sind.

### __Aufbau der App__
Die Programmierung der App erfolgt in Java. Das Frontend kann mit XML-Files bestimmt werden.  
Sobald der Benutzer die App startet wird überprüft ob er angemeldet ist. Falls das nicht der Fall ist, wird die Login-View gestartet, in der der Benutzer seine vorhanden Anmeldedaten (Email, Passwort) eingeben muss. -> Da ein Registrieren in der App nicht Teil der Anforderungen war, wurde es weggelassen. Registriert werden kann ein Benutzer manuell über die Firebase-Console (Alle Firebase Einstellungen dort zu finden.)  

In der App werden dem Benutzer alle seine individuellen Tasks angezeigt. Diese können gestartet/gestoppt und final beendet werden.  
Sobald sie beendet sind findet der Benutzer sie in einem anderen Fenster mit der dazugehörigen Bearbeitungsdauer.

#### Wichtige Android-Methoden
Es gibt die Möglichkeit Code beim Erstellen und beim Öffnen der App ausführen zu lassen. Dieser muss dafür in der Methode __onCreate()__ oder in der __onStart()__ Methode sein.

### __Firebase-Komponenten verwenden__
Damit die Firebase-Komponenten in der App verwendet werden können müssen im _app/build.gradle_ File folgende dependencies eingefügt werden:  
```
dependencies{
    ...
    implementation 'com.google.firebase:firebase-database:16.0.6'
    implementation 'com.google.firebase:firebase-core:16.0.4'
    implementation 'com.google.firebase:firebase-auth:16.1.0'
    implementation 'com.google.firebase:firebase-firestore:18.1.0'
}
```
Weiters muss ganz unten im File folgende Line hinzugefügt werden:  
```
apply plugin: 'com.google.gms.google-services'
```

### __Authentifizierung__
Für die Authentifizierung wurde __Firebase-Authentication__ verwendet. Um diese Firebase-Komponente zu verwenden wird beim Erstellen der App, in der Klasse _EmailPasswordActivity_ in der _onCreate()_-Methode, die private Variable _mAuth_ initialisiert. Diese wird im späteren Verlauf verwendet, um die Authentifizierungs-Methoden von Firebase ansprechen zu können.
```java
// Initialize Firebase Auth
mAuth = FirebaseAuth.getInstance();
```
__Beim Starten__ der App wird überprüft ob der Benutzer bereits bei Firebase angemeldet ist. Falls nicht, liefert die Methode _getCurrentUser()_ _null_ zurück. 
```java
@Override
public void onStart() {
    super.onStart();

    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = mAuth.getCurrentUser();
    updateUI(currentUser, null);
}
```
Gibt es einen angemelden User, dann werden dessen Tasks angezeigt. Andernfalls wird ein Fenster geöffnet, in dem der Benutzer sich anmelden kann.  

Mit der Methode _signInWithEmailAndPassword()_ wird die Authentifizierung bei Firebase gemacht.
```java
mAuth.signInWithEmailAndPassword(email, password)
```
An diese Methode kann ein _OnCompleteListener_ gehängt werden. Dieser kann dann auf das Authentifizierungsergebnis (eingeloggt/Fehler) reagieren.
```java
.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
    if (task.isSuccessful()) {
        // ...
    } else {
        // ...
    }
```
Für das __Ausloggen__ wird die Methode _mAuth.signOut()_ verwendet.

### __Neue Tasks__

### __Task starten__

### __Taks stoppen__

### __Task beenden__

## Quellen
