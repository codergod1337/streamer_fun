# Whisper Real-Time Speech Recognition

Dieses Repository enthält ein Python-Skript für die Echtzeit-Spracherkennung mit OpenAI Whisper.

## Voraussetzungen

1. **Git** zum Klonen des Repositories:

   * Windows: [Git for Windows herunterladen](https://git-scm.com/downloads/win)

2. **Miniconda** für die Python-Umgebung:

   * Windows: [Miniconda3 (64-bit) herunterladen](https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe)

## Installation und Setup

1. **Repository klonen**

   Öffne eine Eingabeaufforderung oder PowerShell und führe aus:

   ```bash
   git clone https://github.com/dein-benutzername/dein-repo.git
   cd dein-repo
   ```

2. **Virtuelle Umgebung erstellen**

   Im geklonten Ordner führt ein Doppelklick auf `1.bat` (bzw. `create_whisper_env.bat`) automatisch aus:

   * Es wird die Conda-Umgebung `whisper` erstellt (falls noch nicht vorhanden) und aktiviert.
   * Anschließend werden alle notwendigen Python-Pakete per `pip install` nachgezogen.

   ```batch
   1.bat
   ```

3. **Script ausführen**

   Nach erfolgreicher Erstellung und Aktivierung der Umgebung startet dein Skript:

   ```bash
   python run2.py oder Doppelklick auf `run.bat`
   ```

   Folge den Konsolen-Anweisungen, um das Mikrofon auszuwählen und die Transkription zu starten.

## Hinweise

* Stelle sicher, dass dein Mikrofon korrekt am System angeschlossen ist.
* Bei Problemen mit Abhängigkeiten lösche das Environment(`conda env remove -n whisper`) und führe `1.bat` erneut aus.


