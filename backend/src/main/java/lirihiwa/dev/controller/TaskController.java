package lirihiwa.dev.controller;

import lirihiwa.dev.service.TaskService;
import lirihiwa.dev.task.dto.AssignEmployeeRequest;
import lirihiwa.dev.task.dto.TaskResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/meetings/{meetingId}/tasks")
    public List<TaskResponse> getByMeeting(@PathVariable Long meetingId) {
        return taskService.findByMeeting(meetingId);
    }

    @PostMapping("/tasks/{id}/confirm")
    public TaskResponse confirm(@PathVariable Long id) {
        return taskService.confirm(id);
    }

    @PostMapping("/tasks/{id}/reject")
    public TaskResponse reject(@PathVariable Long id) {
        return taskService.reject(id);
    }

    @PutMapping("/tasks/{id}/assignee")
    public TaskResponse assign(@PathVariable Long id, @RequestBody AssignEmployeeRequest request) {
        return taskService.assign(id, request.employeeId());
    }
}
