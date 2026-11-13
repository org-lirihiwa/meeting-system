package lirihiwa.dev.controller;

import lirihiwa.dev.service.SpeakerService;
import lirihiwa.dev.speaker.dto.SpeakerResponse;
import lirihiwa.dev.speaker.dto.UpdateSpeakerRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @GetMapping("/meetings/{meetingId}/speakers")
    public List<SpeakerResponse> getByMeeting(@PathVariable Long meetingId) {
        return speakerService.findByMeeting(meetingId);
    }

    @PutMapping("/speakers/{id}")
    public SpeakerResponse update(@PathVariable Long id, @RequestBody UpdateSpeakerRequest request) {
        return speakerService.update(id, request);
    }
}
