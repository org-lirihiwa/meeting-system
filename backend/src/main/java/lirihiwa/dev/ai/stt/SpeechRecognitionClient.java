package lirihiwa.dev.ai.stt;

import java.io.InputStream;

public interface SpeechRecognitionClient {

    TranscriptionResult transcribe(InputStream audio, String fileName);
}
