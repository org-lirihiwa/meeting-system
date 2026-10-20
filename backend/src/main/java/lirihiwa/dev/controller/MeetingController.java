package lirihiwa.dev.controller;

import lirihiwa.dev.meeting.Meeting;
import lirihiwa.dev.meeting.MeetingRepository;
import lirihiwa.dev.meeting.dto.CreateMeetingRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final JdbcTemplate jdbcTemplate;
    private final MeetingRepository meetingRepository;

    public MeetingController(JdbcTemplate jdbcTemplate, MeetingRepository meetingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.meetingRepository = meetingRepository;
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
        Meeting meeting = new Meeting(request.title());
        return meetingRepository.save(meeting);
    }

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingRepository.findAll();
    }
}
