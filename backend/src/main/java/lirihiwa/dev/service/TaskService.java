package lirihiwa.dev.service;

import lirihiwa.dev.task.Task;
import lirihiwa.dev.task.TaskRepository;
import lirihiwa.dev.task.TaskStatus;
import lirihiwa.dev.task.dto.TaskResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.mail.MailException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByMeeting(Long meetingId) {
        return taskRepository.findByMeetingId(meetingId).stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional
    public TaskResponse confirm(Long taskId) {
        Task task = findDraft(taskId);
        if (task.getEmployee() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Assign an employee before confirming the task"
            );
        }
        try {
            notificationService.sendTaskAssigned(task);
        } catch (MailException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to send email notification",
                    e
            );
        }
        task.setStatus(TaskStatus.CONFIRMED);
        task.setEmailSent(true);
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse reject(Long taskId) {
        Task task = findDraft(taskId);
        task.setStatus(TaskStatus.REJECTED);
        return TaskResponse.from(task);
    }

    private Task findDraft(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (task.getStatus() != TaskStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task is not in DRAFT status");
        }
        return task;
    }
}
