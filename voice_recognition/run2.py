"""
Real-time microphone speech recognition using OpenAI Whisper.

Features:
- Lists available audio input devices for selection.
- Clears console (supports Windows and Unix terminals).
- Displays selected microphone name at top.
- Continuously records audio in chunks and transcribes using Whisper.
- Prints recognized text with detected language in parentheses.
- Configurable Whisper settings for maximum flexibility.
"""
# DOWNLOAD https://git-scm.com/downloads/win
# DOWNLOAD: https://repo.anaconda.com/miniconda/Miniconda3-latest-Windows-x86_64.exe
# pip install torch==2.5.1+cu121 torchvision==0.20.1+cu121 torchaudio==2.5.1+cu121 --index-url https://download.pytorch.org/whl/cu121
# pip install openai-whisper sounddevice numpy librosa requests

import os
import sys
import json  # für persistenten State
import whisper
import sounddevice as sd
import numpy as np
try:
    import librosa
except ImportError:
    librosa = None  # Resampling requires librosa; optional

import torch, torchvision, torchaudio
import requests

# Datei für persistenten Log (Counter und Transkripte)
LOG_FILE = "log.json"


def load_state(file_path):
    """Lädt Counter und Transkripte aus LOG_FILE oder initialisiert sie."""
    if os.path.isfile(file_path):
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    else:
        return {"lauf": 0, "transcripts": []}


def save_state(state, file_path):
    """Speichert Counter und Transkripte in LOG_FILE."""
    with open(file_path, 'w', encoding='utf-8') as f:
        json.dump(state, f, ensure_ascii=False, indent=2)


def _check_cuda():
    """
    Prüft, ob CUDA verfügbar ist und gibt Versionen aus.
    """
    print("CUDA verfügbar:", torch.cuda.is_available())
    print("Torch CUDA-Version:", torch.version.cuda)
    print("Torchvision-Version:", torchvision.__version__)
    print("Torchaudio-Version:", torchaudio.__version__)


def list_input_devices():
    """
    Fragt alle Audio-Devices ab und gibt jene mit Eingangs-Kanälen aus.
    Gibt Index und Name auf der Konsole aus.
    """
    devices = sd.query_devices()
    input_devices = []
    print("Available audio input devices:")
    for idx, dev in enumerate(devices):
        if dev['max_input_channels'] > 0:
            input_devices.append((idx, dev))
            print(f"  {idx}: {dev['name']} ({dev['max_input_channels']} channels)")
    if not input_devices:
        print("No input devices found.")
        sys.exit(1)
    return input_devices


def clear_console():
    """
    Löscht den Terminal-Bildschirm (Windows und Unix-kompatibel).
    """
    command = 'cls' if os.name == 'nt' else 'clear'
    os.system(command)


def transcribe_buffer(
    model,
    audio_buffer: np.ndarray,
    original_sr: int,
    whisper_sr: int = 16000,
    language: str = None,
    task: str = "transcribe",
    initial_prompt: str = None,
    condition_on_previous_text: bool = True,
    temperature: list = [0.0],
    best_of: int = 5,
    beam_size: int = 5,
    patience: float = None,
    length_penalty: float = 1.0,
    compression_ratio_threshold: float = 2.4,
    logprob_threshold: float = -1.0,
    no_speech_threshold: float = 0.6,
    suppress_tokens: list = None,
    fp16: bool = True
) -> dict:
    """
    Transkribiert ein Audio-Buffer mit Whisper und detaillierten Parametern.
    Liefert das Ergebnis-Dict von Whisper zurück.
    """
    if original_sr != whisper_sr:
        if librosa is None:
            raise RuntimeError("Librosa benötigt für Resampling. Install via pip install librosa.")
        audio_buffer = librosa.resample(audio_buffer, orig_sr=original_sr, target_sr=whisper_sr)
        audio_buffer = np.ascontiguousarray(audio_buffer)

    if audio_buffer.ndim > 1:
        audio_buffer = audio_buffer.mean(axis=1)
    audio_buffer = audio_buffer.astype(np.float32)

    result = model.transcribe(
        audio=audio_buffer,
        language=language,                        # ISO-Code (z.B. 'en','de') oder None für Auto-Erkennung
        task=task,                                # 'transcribe' (STT) oder 'translate'
        initial_prompt=initial_prompt,            # Kontext-Prompt zur Steuerung
        condition_on_previous_text=condition_on_previous_text,  # Vorherigen Text berücksichtigen
        temperature=temperature,                  # Sampling-Temperatur (höher = mehr Variation)
        best_of=best_of,                          # Zahl der Kandidaten zur Auswahl des Besten
        beam_size=beam_size,                      # Breite des Beam-Search (höher = gründlicher)
        patience=patience,                        # Early-Stopping-Patience im Beam-Search
        length_penalty=length_penalty,            # Strafe für Textlänge (länger/kürzer)
        compression_ratio_threshold=compression_ratio_threshold, # Abbruch bei zu hoher Kompression
        logprob_threshold=logprob_threshold,      # Abbruch bei niedrigen Log-Wahrscheinlichkeiten
        no_speech_threshold=no_speech_threshold,  # Schwellenwert, um Stille zu erkennen
        suppress_tokens=suppress_tokens,          # Tokens, die unterdrückt werden sollen
        fp16=fp16                                 # FP16 für schnellere GPU-Inferenz
    )
    return result


def main():
    _check_cuda()

    # Zustand laden und Counter initialisieren
    state = load_state(LOG_FILE)
    lauf = state['lauf']
    print(f"State geladen: lauf={lauf}, Einträge={len(state['transcripts'])}")

    # Modell-Auswahl
    available_models = [
        ("tiny",  "39M parameters, ~1 GB VRAM"),
        ("base",  "74M parameters, ~1 GB VRAM"),
        ("small", "244M parameters, ~2 GB VRAM"),
        ("medium","769M parameters, ~5 GB VRAM"),
        ("large", "1550M parameters, ~10 GB VRAM"),
        ("turbo", "809M parameters, ~6 GB VRAM, ~8x speed")
    ]
    print("Available Whisper models:")
    for i, (m, info) in enumerate(available_models):
        print(f"  {i}: {m} ({info})")
    choice = input("\nEnter the number of the model to use and press Enter: ").strip()
    try:
        model_index = int(choice)
        MODEL_NAME = available_models[model_index][0]
    except (ValueError, IndexError):
        print("Ungültige Modell-Auswahl.")
        sys.exit(1)

    # Audio-Geräte-Auswahl
    devices = list_input_devices()
    choice = input("\nEnter the number of the device to use and press Enter: ").strip()
    try:
        device_index = int(choice)
    except ValueError:
        print("Ungültige Geräte-Auswahl.")
        sys.exit(1)

    clear_console()
    selected_info = sd.query_devices(device_index)
    mic_name = selected_info['name']
    print(f"Selected microphone channel: {mic_name}")
    print(f"Using Whisper model: {MODEL_NAME}\n")

    WHISPER_SR     = 16000
    CHUNK_DURATION = 5.0
    CHANNELS       = 1

    print("Loading Whisper model... (dies kann eine Weile dauern)")
    model = whisper.load_model(MODEL_NAME)
    print(f"Model '{MODEL_NAME}' loaded. Starting transcription.\n")

    default_sr = int(selected_info['default_samplerate'])

    try:
        while True:
            # Aufnahme
            num_frames = int(default_sr * CHUNK_DURATION)
            audio = sd.rec(frames=num_frames,
                           samplerate=default_sr,
                           channels=CHANNELS,
                           dtype='float32',
                           device=device_index)
            sd.wait()
            audio = np.squeeze(audio)

            # Transkription
            result = transcribe_buffer(
                model=model,
                audio_buffer=audio,
                original_sr=default_sr,
                whisper_sr=WHISPER_SR
            )
            text = result.get("text", "").strip()
            #lang = result.get("language", "unknown")
            #print(f"{text} ({lang})")
            print(f"{text}")

            # State aktualisieren und speichern
            lauf += 1
            state['lauf'] = lauf
            state['transcripts'].append({
                'id': lauf,
                'text': text
            })
            save_state(state, LOG_FILE)

            # Unverändertes Weiterleiten ans Backend
            send_to_backend(text)

    except KeyboardInterrupt:
        print("\nTranscription stopped by user. Exiting.")
        sys.exit(0)


def send_to_backend(text):
    # für jedes Wort einzeln senden, falls gewünscht
    try:
        requests.post(
            url="http://localhost:6969/api/rawdata/text",
            json={"value": text},
            timeout=0.5
        )
    except Exception:
        pass

if __name__ == "__main__":
    main()