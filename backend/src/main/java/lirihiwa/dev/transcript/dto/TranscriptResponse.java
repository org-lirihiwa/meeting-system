package lirihiwa.dev.transcript.dto;

import lirihiwa.dev.transcript.Transcript;

public record TranscriptResponse(
        Long id,
        Long speakerId,
        String speakerName,
        String text,
        double startOffset,
        double endOffset,
        Double confidence
) {

    public static TranscriptResponse from(Transcript transcript) {
        return new TranscriptResponse(
                transcript.getId(),
                transcript.getSpeaker().getId(),
                transcript.getSpeaker().getDisplayName(),
                transcript.getText(),
                transcript.getStartOffset(),
                transcript.getEndOffset(),
                transcript.getConfidence()
        );
    }
}
