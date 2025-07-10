@echo off
REM run.bat – startet die Transkription und hält das Fenster offen

REM 1) Conda-Umgebung aktivieren
call conda activate whisper
if %ERRORLEVEL% neq 0 (
echo Fehler beim Aktivieren der Conda-Umgebung "whisper".
pause
exit /b %ERRORLEVEL%
)

REM 2) Transkriptions-Skript starten
echo Starte Echtzeit-Transkription...
python run2.py

REM 3) Nach Beenden (z. B. Ctrl+C) Fenster offen halten
echo.
echo Transkription beendet oder unterbrochen.
pause