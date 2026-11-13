package lirihiwa.dev.meeting.dto;

import lirihiwa.dev.meeting.MeetingStatus;
import lirihiwa.dev.speaker.dto.SpeakerResponse;
import lirihiwa.dev.task.dto.TaskResponse;
import lirihiwa.dev.transcript.dto.TranscriptResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ProtocolResponse(
        Long meetingId,
        String title,
        String description,
        LocalDateTime meetingDate,
        MeetingStatus status,
        List<SpeakerResponse> speakers,
        List<TranscriptResponse> transcripts,
        List<TaskResponse> tasks
) {
}
