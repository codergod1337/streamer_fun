@echo off
setlocal

REM Name der Conda-Umgebung
set "ENV_NAME=whisper"

REM Pfad, wo Conda Envs liegen
for /f "tokens=2 delims==" %%D in ('conda info --base') do set "CONDA_BASE=%%D"
set "ENV_PATH=%CONDA_BASE%\envs\%ENV_NAME%"

REM Prüfen, ob ENV_PATH existiert
if exist "%ENV_PATH%" (
    echo Conda-Environment "%ENV_NAME%" existiert bereits.
) else (
    echo Erstelle Conda-Environment "%ENV_NAME%" mit Python 3.10...
    call conda create -n %ENV_NAME% python=3.10 -y
    if errorlevel 1 (
        echo FEHLER beim Erstellen des Environments. Abbruch.
        pause
        exit /b 1
    )
)

REM Env aktivieren
call conda activate %ENV_NAME%
if errorlevel 1 (
    echo FEHLER beim Aktivieren des Environments. Abbruch.
    pause
    exit /b 1
)

REM Pip-Installationen
echo Installiere GPU-Torch + CUDA...
pip install torch==2.5.1+cu121 torchvision==0.20.1+cu121 torchaudio==2.5.1+cu121 --index-url https://download.pytorch.org/whl/cu121
echo Installiere restliche Abhängigkeiten...
pip install openai-whisper sounddevice numpy librosa requests

echo.
echo Fertig! Conda-Env "%ENV_NAME%" ist jetzt aktiv aktiv.
pause
endlocal