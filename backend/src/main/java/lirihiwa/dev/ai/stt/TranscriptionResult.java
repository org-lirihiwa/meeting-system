package lirihiwa.dev.ai.stt;

import java.util.List;

public record TranscriptionResult(
        List<TranscriptSegment> segments
) {
}
