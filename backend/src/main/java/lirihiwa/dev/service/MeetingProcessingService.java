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

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MeetingProcessingService {

    private final MeetingRepository meetingRepository;
    private final SpeakerRepository speakerRepository;
    private final TranscriptRepository transcriptRepository;

    public MeetingProcessingService(MeetingRepository meetingRepository,
                                    SpeakerRepository speakerRepository,
                                    TranscriptRepository transcriptRepository) {
        this.meetingRepository = meetingRepository;
        this.speakerRepository = speakerRepository;
        this.transcriptRepository = transcriptRepository;
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
}
