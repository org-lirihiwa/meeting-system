package lirihiwa.dev.controller;

import lirihiwa.dev.service.MeetingService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import lirihiwa.dev.meeting.dto.CreateMeetingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import lirihiwa.dev.meeting.dto.ProtocolResponse;
import lirihiwa.dev.service.ProtocolService;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final JdbcTemplate jdbcTemplate;
    private final MeetingRepository meetingRepository;
    private final MeetingService meetingService;
    private final ProtocolService protocolService;

    public MeetingController(JdbcTemplate jdbcTemplate,
                             MeetingRepository meetingRepository,
                             MeetingService meetingService,
                             ProtocolService protocolService) {
        this.jdbcTemplate = jdbcTemplate;
        this.meetingRepository = meetingRepository;
        this.meetingService = meetingService;
        this.protocolService = protocolService;
    }

    @GetMapping("/health/db")
    public String checkDatabase() {
        return jdbcTemplate.queryForObject(
                "SELECT 'Connected to PostgreSQL'",
                String.class
        );
    }

    @PostMapping
    public Meeting createMeeting(@RequestBody CreateMeetingRequest request) {
        Meeting meeting = new Meeting(request.title(), request.description(), request.meetingDate());
        return meetingRepository.save(meeting);
    }

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingRepository.findAll();
    }

    @GetMapping("/{id}")
    public Meeting getMeeting(@PathVariable Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Meeting not found"
                ));
    }

    @PostMapping(value = "/{id}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Meeting uploadAudio(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return meetingService.uploadAudio(id, file);
    }

    @GetMapping("/{id}/protocol")
    public ProtocolResponse getProtocol(@PathVariable Long id) {
        return protocolService.getProtocol(id);
    }
}
