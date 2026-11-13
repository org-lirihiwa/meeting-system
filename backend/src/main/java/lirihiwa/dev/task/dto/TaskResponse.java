package lirihiwa.dev.task.dto;

import lirihiwa.dev.task.Task;
import lirihiwa.dev.task.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        Long meetingId,
        Long transcriptId,
        Long employeeId,
        String employeeName,
        String rawAssigneeText,
        String text,
        String topic,
        String territory,
        String deadline,
        TaskStatus status,
        boolean emailSent,
        LocalDateTime createdAt
) {

    public static TaskResponse from(Task task) {
        var employee = task.getEmployee();
        return new TaskResponse(
                task.getId(),
                task.getMeeting().getId(),
                task.getTranscript() != null ? task.getTranscript().getId() : null,
                employee != null ? employee.getId() : null,
                employee != null ? employee.getLastName() + " " + employee.getFirstName() : null,
                task.getRawAssigneeText(),
                task.getText(),
                task.getTopic(),
                task.getTerritory(),
                task.getDeadline(),
                task.getStatus(),
                task.isEmailSent(),
                task.getCreatedAt()
        );
    }
}
