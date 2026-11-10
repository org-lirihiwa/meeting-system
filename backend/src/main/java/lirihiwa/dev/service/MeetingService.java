package lirihiwa.dev.service;

import lirihiwa.dev.config.RabbitConfig;
import lirihiwa.dev.event.MeetingAudioUploadedEvent;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final FileStorageService fileStorageService;
    private final RabbitTemplate rabbitTemplate;

    public MeetingService(MeetingRepository meetingRepository,
                          FileStorageService fileStorageService,
                          RabbitTemplate rabbitTemplate) {
        this.meetingRepository = meetingRepository;
        this.fileStorageService = fileStorageService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public Meeting uploadAudio(Long meetingId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Audio file is empty");
        }

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Meeting not found"
                ));

        String objectKey = fileStorageService.upload("meetings/" + meetingId, file);
        meeting.setAudioUrl(objectKey);

        MeetingAudioUploadedEvent event = new MeetingAudioUploadedEvent(meetingId, objectKey);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                rabbitTemplate.convertAndSend(RabbitConfig.AUDIO_UPLOADED_QUEUE, event);
            }
        });
        return meeting;
    }
}
