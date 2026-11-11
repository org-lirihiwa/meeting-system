package lirihiwa.dev.worker;

import lirihiwa.dev.ai.stt.SpeechRecognitionClient;
import lirihiwa.dev.ai.stt.TranscriptionResult;
import lirihiwa.dev.config.RabbitConfig;
import lirihiwa.dev.event.MeetingAudioUploadedEvent;
import lirihiwa.dev.service.FileStorageService;
import lirihiwa.dev.service.MeetingProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lirihiwa.dev.ai.llm.ExtractedTask;
import lirihiwa.dev.ai.llm.TaskExtractor;

import java.util.List;

import java.io.InputStream;

@Component
public class MeetingAudioWorker {

    private static final Logger log = LoggerFactory.getLogger(MeetingAudioWorker.class);

    private final MeetingProcessingService processingService;
    private final FileStorageService fileStorageService;
    private final SpeechRecognitionClient speechRecognitionClient;
    private final TaskExtractor taskExtractor;

    public MeetingAudioWorker(MeetingProcessingService processingService,
                              FileStorageService fileStorageService,
                              SpeechRecognitionClient speechRecognitionClient, TaskExtractor taskExtractor) {
        this.processingService = processingService;
        this.fileStorageService = fileStorageService;
        this.speechRecognitionClient = speechRecognitionClient;
        this.taskExtractor = taskExtractor;
    }

    @RabbitListener(queues = RabbitConfig.AUDIO_UPLOADED_QUEUE)
    public void handle(MeetingAudioUploadedEvent event) {
        Long meetingId = event.meetingId();

        String audioKey = processingService.startProcessing(meetingId);
        if (audioKey == null) {
            log.warn("Meeting {} not found or has no audio, skipping event", meetingId);
            return;
        }
        log.info("Meeting {} moved to PROCESSING (audio: {})", meetingId, audioKey);

        try {
            String fileName = audioKey.substring(audioKey.lastIndexOf('/') + 1);
            TranscriptionResult result;
            try (InputStream audio = fileStorageService.download(audioKey)) {
                result = speechRecognitionClient.transcribe(audio, fileName);
            }
            processingService.saveResult(meetingId, result);
            log.info("Meeting {} transcribed: {} segments", meetingId, result.segments().size());

            List<String> lines = processingService.loadTranscriptLines(meetingId);
            List<ExtractedTask> tasks = taskExtractor.extract(lines);
            processingService.saveTasks(meetingId, tasks);
            log.info("Meeting {} COMPLETED: {} tasks extracted", meetingId, tasks.size());
        } catch (Exception e) {
            log.error("Meeting {} processing failed", meetingId, e);
            processingService.markFailed(meetingId);
        }
    }
}
