# conda create -n whisper python=3.10
# conda activate whisper
# pip install git+https://github.com/openai/whisper.git
# pip install sounddevice
# pip install numpy
# pip install requests
# pip install torch torchaudio silero-vad rich pynput pygetwindow psutil
# pip install pygetwindow

import os
import sys
import time
import threading
from collections import deque

import sounddevice as sd
import numpy as np
import whisper

from pynput.keyboard import Listener, Key
from rich.console import Console, Group
from rich.live import Live
from rich.panel import Panel
from rich.text import Text

console = Console()
running = True
paused = False
device_index = None
key_press_time = {}

def show_hold_progress_ascii(key_name: str, start: float, total: float = 5.0):
    """Zeichnet einen einzigen, sich überschreibenden ASCII-Balken."""
    bar_width = 40
    # 1s still warten
    while time.time() - start < 1.0:
        if key_name not in key_press_time:
            sys.stdout.write("\r" + " "*(bar_width+30) + "\r")
            sys.stdout.flush()
            return
        time.sleep(0.05)
    end = start + total
    while time.time() < end:
        if key_name not in key_press_time:
            sys.stdout.write("\r" + " "*(bar_width+30) + "\r")
            sys.stdout.flush()
            return
        frac = (time.time() - (start+1.0)) / (total-1.0)
        frac = min(max(frac, 0.0), 1.0)
        filled = int(bar_width * frac)
        bar = "█"*filled + "░"*(bar_width-filled)
        sys.stdout.write(f"\rHalte [{key_name.upper():>5}] [{bar}]")
        sys.stdout.flush()
        time.sleep(0.05)
    # clear line
    sys.stdout.write("\r" + " "*(bar_width+30) + "\r")
    sys.stdout.flush()

    global paused, running
    if key_name == "space":
        paused = not paused
        console.print(f"[bold green]↻ Aufnahme {'PAUSIERT' if paused else 'FORTGESETZT'}[/]")
    elif key_name == "x":
        console.print("[bold yellow]↻ Reset zur Geräteauswahl[/]")
        running = False

def key_listener():
    def on_press(key):
        kn = None
        if key == Key.space:
            kn = "space"
        elif hasattr(key, "char") and key.char == "x":
            kn = "x"
        if kn and kn not in key_press_time:
            key_press_time[kn] = time.time()
            threading.Thread(
                target=show_hold_progress_ascii,
                args=(kn, key_press_time[kn]),
                daemon=True
            ).start()

    def on_release(key):
        kn = None
        if key == Key.space:
            kn = "space"
        elif hasattr(key, "char") and key.char == "x":
            kn = "x"
        if kn and kn in key_press_time:
            del key_press_time[kn]

    Listener(on_press=on_press, on_release=on_release, daemon=True).start()

def list_input_devices():
    return [
        (i, dev["name"])
        for i, dev in enumerate(sd.query_devices())
        if dev["max_input_channels"] > 0
    ]

def select_device(devices):
    console.clear()
    console.print("[bold cyan]🎙️ Verfügbare Audio-Eingabegeräte:[/]")
    for idx, name in devices:
        console.print(f"[green]{idx}[/green]: {name}")
    while True:
        choice = console.input("[bold green]➡ ID wählen:[/] ")
        if choice.isdigit() and any(int(choice) == d for d, _ in devices):
            return int(choice)
        console.print("[red]Ungültige Auswahl![/]")

def measure_baseline(duration=2.0, samplerate=16000, device=None):
    console.print("[dim]Messe Hintergrundpegel... bitte ruhig bleiben[/dim]")
    rec = sd.rec(
        int(duration * samplerate),
        samplerate=samplerate,
        channels=1,
        dtype="float32",
        device=device
    )
    sd.wait()
    rms = np.sqrt((rec**2).mean())
    console.print(f"[dim]Baseline RMS: {rms:.6f}[/dim]")
    return rms

def audio_stream(baseline, factor=1.5):
    global running, paused
    model = whisper.load_model("base")
    threshold = baseline + 0.02  # absolute Schwellwert über Rauschen
    samplerate = 16000
    buffer = []
    recognized_words = deque(maxlen=20)
    current_level = 0.0

    def read_loop():
        nonlocal buffer, current_level
        stream = sd.InputStream(
            device=device_index,
            channels=1,
            samplerate=samplerate,
            blocksize=1024
        )
        with stream:
            while running:
                data, _ = stream.read(1024)
                vol = np.sqrt((data**2).mean())
                current_level = vol
                if not paused:
                    if vol > threshold:
                        buffer.append(data.copy())
                    elif buffer:
                        segment = np.concatenate(buffer, axis=0).flatten()
                        buffer.clear()
                        res = model.transcribe(segment.astype(np.float32), language=None, fp16=False)
                        lang = res.get("language", None)
                        # nur Deutsch oder Englisch zulassen
                        if lang not in ("de", "en"):
                            continue  # überspringe dieses Segment
                        
                        text = res["text"].strip()
                        for w in text.split():
                            recognized_words.append(w)
                time.sleep(0.005)

    threading.Thread(target=read_loop, daemon=True).start()

    bar_width = 40
    with Live(console=console, refresh_per_second=15) as live:
        while running:
            norm = min(current_level * 50, 1.0)
            filled = int(bar_width * norm)
            bar_str = "█" * filled + "░" * (bar_width - filled)
            bar = f"[bright_black]{bar_str}[/bright_black]" if paused else f"[cyan]{bar_str}[/cyan]"
            state = "[bright_black]⏸ PAUSIERT[/]" if paused else "[bold green]🔴 AUFNAHME[/]"
            instr = "[dim]Space 5s: Pause/Start | x 5s: Neu wählen[/dim]"

            panel = Panel.fit(
                f"[bold yellow]🎤 Gerät:[/] {sd.query_devices(device_index)['name']}\n"
                f"{state}\n\n{bar}\n\n{instr}",
                title="🎧 Streamer Mic Monitor",
                border_style="blue"
            )
            log = Text("\n".join(f"Wort erkannt: {w}" for w in list(recognized_words)[-5:]), style="white")
            live.update(Group(panel, log))
            time.sleep(0.05)

def main():
    os.system("cls" if os.name == "nt" else "clear")
    key_listener()
    global running, paused, device_index
    while True:
        devices = list_input_devices()
        if not devices:
            console.print("[red]❌ Kein Mikrofon gefunden[/red]")
            return
        device_index = select_device(devices)
        os.system("cls" if os.name == "nt" else "clear")
        running = True
        paused = False
        baseline = measure_baseline(device=device_index)
        audio_stream(baseline)
        os.system("cls" if os.name == "nt" else "clear")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        os.system("cls" if os.name == "nt" else "clear")
        console.print("[bold red]🛑 Erkennung gestoppt.[/bold red]")

