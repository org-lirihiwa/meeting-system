package lirihiwa.dev.meeting.dto;

import java.time.LocalDateTime;

public record CreateMeetingRequest(
        String title,
        String description,
        LocalDateTime meetingDate
) {
}
