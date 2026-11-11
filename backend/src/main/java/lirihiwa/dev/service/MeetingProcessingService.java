package lirihiwa.dev.service;

import lirihiwa.dev.ai.stt.TranscriptSegment;
import lirihiwa.dev.ai.stt.TranscriptionResult;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import lirihiwa.dev.meeting.MeetingStatus;
import lirihiwa.dev.speaker.Speaker;
import lirihiwa.dev.speaker.SpeakerRepository;
import lirihiwa.dev.transcript.Transcript;
import lirihiwa.dev.transcript.TranscriptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lirihiwa.dev.ai.llm.ExtractedTask;
import lirihiwa.dev.task.Task;
import lirihiwa.dev.task.TaskRepository;

import java.util.List;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MeetingProcessingService {

    private final MeetingRepository meetingRepository;
    private final SpeakerRepository speakerRepository;
    private final TranscriptRepository transcriptRepository;
    private final TaskRepository taskRepository;

    public MeetingProcessingService(MeetingRepository meetingRepository,
                                    SpeakerRepository speakerRepository,
                                    TranscriptRepository transcriptRepository, TaskRepository taskRepository) {
        this.meetingRepository = meetingRepository;
        this.speakerRepository = speakerRepository;
        this.transcriptRepository = transcriptRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Переводит совещание в PROCESSING и возвращает ключ аудио, либо null, если обрабатывать нечего.
     */
    @Transactional
    public String startProcessing(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElse(null);
        if (meeting == null || meeting.getAudioUrl() == null) {
            return null;
        }

        taskRepository.deleteByMeetingId(meetingId);
        transcriptRepository.deleteByMeetingId(meetingId);
        speakerRepository.deleteByMeetingId(meetingId);

        meeting.setStatus(MeetingStatus.PROCESSING);
        return meeting.getAudioUrl();
    }

    @Transactional
    public void saveResult(Long meetingId, TranscriptionResult result) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();

        Map<String, Speaker> speakers = new LinkedHashMap<>();
        for (TranscriptSegment segment : result.segments()) {
            Speaker speaker = speakers.get(segment.speakerLabel());
            if (speaker == null) {
                String displayName = "Диктор " + (speakers.size() + 1);
                speaker = speakerRepository.save(new Speaker(meeting, segment.speakerLabel(), displayName));
                speakers.put(segment.speakerLabel(), speaker);
            }
            transcriptRepository.save(new Transcript(
                    meeting,
                    speaker,
                    segment.text(),
                    segment.startOffset(),
                    segment.endOffset(),
                    segment.confidence()
            ));
        }

        meeting.setStatus(MeetingStatus.COMPLETED);
    }

    @Transactional
    public void markFailed(Long meetingId) {
        meetingRepository.findById(meetingId)
                .ifPresent(meeting -> meeting.setStatus(MeetingStatus.FAILED));
    }

    @Transactional(readOnly = true)
    public List<String> loadTranscriptLines(Long meetingId) {
        return transcriptRepository.findByMeetingIdOrderByStartOffsetAsc(meetingId).stream()
                .map(t -> t.getSpeaker().getDisplayName() + ": " + t.getText())
                .toList();
    }

    @Transactional
    public void saveTasks(Long meetingId, List<ExtractedTask> extracted) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow();
        List<Transcript> transcripts = transcriptRepository.findByMeetingIdOrderByStartOffsetAsc(meetingId);

        for (ExtractedTask item : extracted) {
            if (item.text() == null || item.text().isBlank()) {
                continue;
            }
            Transcript source = null;
            Integer index = item.sourceIndex();
            if (index != null && index >= 0 && index < transcripts.size()) {
                source = transcripts.get(index);
            }
            taskRepository.save(new Task(
                    meeting, source, item.assignee(), item.text(),
                    item.topic(), item.territory(), item.deadline()
            ));
        }

        meeting.setStatus(MeetingStatus.COMPLETED);
    }
}
