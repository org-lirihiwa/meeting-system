package lirihiwa.dev.service;

import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import lirihiwa.dev.meeting.dto.ProtocolResponse;
import lirihiwa.dev.speaker.SpeakerRepository;
import lirihiwa.dev.speaker.dto.SpeakerResponse;
import lirihiwa.dev.task.TaskRepository;
import lirihiwa.dev.task.dto.TaskResponse;
import lirihiwa.dev.transcript.TranscriptRepository;
import lirihiwa.dev.transcript.dto.TranscriptResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProtocolService {

    private final MeetingRepository meetingRepository;
    private final SpeakerRepository speakerRepository;
    private final TranscriptRepository transcriptRepository;
    private final TaskRepository taskRepository;

    public ProtocolService(MeetingRepository meetingRepository,
                           SpeakerRepository speakerRepository,
                           TranscriptRepository transcriptRepository,
                           TaskRepository taskRepository) {
        this.meetingRepository = meetingRepository;
        this.speakerRepository = speakerRepository;
        this.transcriptRepository = transcriptRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public ProtocolResponse getProtocol(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        return new ProtocolResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getDescription(),
                meeting.getMeetingDate(),
                meeting.getStatus(),
                speakerRepository.findByMeetingId(meetingId).stream().map(SpeakerResponse::from).toList(),
                transcriptRepository.findByMeetingIdOrderByStartOffsetAsc(meetingId).stream()
                        .map(TranscriptResponse::from).toList(),
                taskRepository.findByMeetingId(meetingId).stream().map(TaskResponse::from).toList()
        );
    }
}
