package lirihiwa.dev.speaker.dto;

import lirihiwa.dev.speaker.Speaker;

public record SpeakerResponse(
        Long id,
        Long meetingId,
        String label,
        String displayName,
        Long employeeId,
        String employeeName
) {

    public static SpeakerResponse from(Speaker speaker) {
        var employee = speaker.getEmployee();
        return new SpeakerResponse(
                speaker.getId(),
                speaker.getMeeting().getId(),
                speaker.getLabel(),
                speaker.getDisplayName(),
                employee != null ? employee.getId() : null,
                employee != null ? employee.getLastName() + " " + employee.getFirstName() : null
        );
    }
}
