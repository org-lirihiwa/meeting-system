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
import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.employee.EmployeeRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;
    private final EmployeeRepository employeeRepository;

    public TaskService(TaskRepository taskRepository, NotificationService notificationService, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
        this.employeeRepository = employeeRepository;
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

    @Transactional
    public TaskResponse assign(Long taskId, Long employeeId) {
        Task task = findDraft(taskId);
        if (employeeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        if (!employee.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee is not active");
        }
        task.setEmployee(employee);
        return TaskResponse.from(task);
    }
}
