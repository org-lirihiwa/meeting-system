package lirihiwa.dev.event;

public record MeetingAudioUploadedEvent(
        Long meetingId,
        String audioKey
) {
}
