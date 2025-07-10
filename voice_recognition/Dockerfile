FROM python:3.10-slim

WORKDIR /app

# System-Bibliotheken für Audio (ffmpeg + libsndfile)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      ffmpeg libsndfile1 portaudio19-dev libportaudio2 \
    && rm -rf /var/lib/apt/lists/*

# Dein Pip-Requirements kopieren und installieren
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --no-cache-dir \
      --extra-index-url https://download.pytorch.org/whl/cu121 \
      -r requirements.txt

# Dein Script kopieren und ausführen
COPY run2.py .
ENV PYTHONUNBUFFERED=1
ENTRYPOINT ["python", "run2.py"]