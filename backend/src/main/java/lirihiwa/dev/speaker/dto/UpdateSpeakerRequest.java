package lirihiwa.dev.speaker.dto;

public record UpdateSpeakerRequest(
        String displayName,
        Long employeeId
) {
}
