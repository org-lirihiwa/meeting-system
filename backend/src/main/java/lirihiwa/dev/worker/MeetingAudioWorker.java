package lirihiwa.dev.worker;

import lirihiwa.dev.config.RabbitConfig;
import lirihiwa.dev.event.MeetingAudioUploadedEvent;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import lirihiwa.dev.meeting.MeetingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MeetingAudioWorker {

    private static final Logger log = LoggerFactory.getLogger(MeetingAudioWorker.class);

    private final MeetingRepository meetingRepository;

    public MeetingAudioWorker(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @RabbitListener(queues = RabbitConfig.AUDIO_UPLOADED_QUEUE)
    @Transactional
    public void handle(MeetingAudioUploadedEvent event) {
        Meeting meeting = meetingRepository.findById(event.meetingId()).orElse(null);
        if (meeting == null) {
            log.warn("Meeting {} not found, skipping event", event.meetingId());
            return;
        }

        meeting.setStatus(MeetingStatus.PROCESSING);
        log.info("Meeting {} moved to PROCESSING (audio: {})", event.meetingId(), event.audioKey());
    }
}
