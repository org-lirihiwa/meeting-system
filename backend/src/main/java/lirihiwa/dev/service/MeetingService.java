package lirihiwa.dev.service;

import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final FileStorageService fileStorageService;

    public MeetingService(MeetingRepository meetingRepository, FileStorageService fileStorageService) {
        this.meetingRepository = meetingRepository;
        this.fileStorageService = fileStorageService;
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
        return meeting;
    }
}
