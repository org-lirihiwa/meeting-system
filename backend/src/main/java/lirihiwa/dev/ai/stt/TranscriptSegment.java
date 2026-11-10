package lirihiwa.dev.ai.stt;

public record TranscriptSegment(
        String speakerLabel,
        String text,
        double startOffset,
        double endOffset,
        Double confidence
) {
}
