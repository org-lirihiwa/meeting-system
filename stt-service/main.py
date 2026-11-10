import math
import os
import tempfile

import torch
from fastapi import FastAPI, File, UploadFile
from faster_whisper import WhisperModel
from pyannote.audio import Pipeline

WHISPER_MODEL = os.getenv("WHISPER_MODEL", "small")
HF_TOKEN = os.getenv("HF_TOKEN")
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
COMPUTE_TYPE = "float16" if DEVICE == "cuda" else "int8"

app = FastAPI()

whisper = WhisperModel(WHISPER_MODEL, device=DEVICE, compute_type=COMPUTE_TYPE)
diarization = Pipeline.from_pretrained(
    "pyannote/speaker-diarization-3.1",
    use_auth_token=HF_TOKEN,
)
diarization.to(torch.device(DEVICE))


def find_speaker(start, end, turns):
    best_label, best_overlap = "SPEAKER_00", 0.0
    for turn_start, turn_end, label in turns:
        overlap = min(end, turn_end) - max(start, turn_start)
        if overlap > best_overlap:
            best_label, best_overlap = label, overlap
    return best_label


@app.get("/health")
def health():
    return {"status": "ok", "device": DEVICE, "model": WHISPER_MODEL}


@app.post("/transcribe")
async def transcribe(file: UploadFile = File(...)):
    suffix = os.path.splitext(file.filename or "")[1] or ".audio"
    with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as tmp:
        tmp.write(await file.read())
        path = tmp.name

    try:
        whisper_segments, _ = whisper.transcribe(path, language="ru", vad_filter=True)
        whisper_segments = list(whisper_segments)

        diarized = diarization(path)
        turns = [
            (turn.start, turn.end, label)
            for turn, _, label in diarized.itertracks(yield_label=True)
        ]

        segments = [
            {
                "speakerLabel": find_speaker(seg.start, seg.end, turns),
                "text": seg.text.strip(),
                "startOffset": seg.start,
                "endOffset": seg.end,
                "confidence": math.exp(seg.avg_logprob),
            }
            for seg in whisper_segments
        ]
        return {"segments": segments}
    finally:
        os.remove(path)
