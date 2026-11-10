FROM python:3.11-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN pip install --no-cache-dir torch==2.5.1 torchaudio==2.5.1 --index-url https://download.pytorch.org/whl/cpu

COPY constraints.txt requirements.txt ./
RUN pip install --no-cache-dir -c constraints.txt -r requirements.txt

RUN python -c "import torchaudio; print('torchaudio', torchaudio.__version__); torchaudio.AudioMetaData"

COPY main.py .

EXPOSE 8000
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
