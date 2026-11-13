package lirihiwa.dev.service;

import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.employee.EmployeeRepository;
import lirihiwa.dev.speaker.Speaker;
import lirihiwa.dev.speaker.SpeakerRepository;
import lirihiwa.dev.speaker.dto.SpeakerResponse;
import lirihiwa.dev.speaker.dto.UpdateSpeakerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;
    private final EmployeeRepository employeeRepository;

    public SpeakerService(SpeakerRepository speakerRepository, EmployeeRepository employeeRepository) {
        this.speakerRepository = speakerRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<SpeakerResponse> findByMeeting(Long meetingId) {
        return speakerRepository.findByMeetingId(meetingId).stream()
                .map(SpeakerResponse::from)
                .toList();
    }

    @Transactional
    public SpeakerResponse update(Long speakerId, UpdateSpeakerRequest request) {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Speaker not found"));

        if (request.displayName() == null || request.displayName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "displayName is required");
        }
        speaker.setDisplayName(request.displayName().trim());

        if (request.employeeId() == null) {
            speaker.setEmployee(null);
        } else {
            Employee employee = employeeRepository.findById(request.employeeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
            speaker.setEmployee(employee);
        }
        return SpeakerResponse.from(speaker);
    }
}
