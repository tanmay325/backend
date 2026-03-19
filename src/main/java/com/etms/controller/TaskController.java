package com.etms.controller;

import com.etms.dto.TaskRequest;
import com.etms.dto.TaskUpdateStatusRequest;
import com.etms.entity.Task;
import com.etms.entity.TaskUpdate;
import com.etms.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable String id,
            @RequestBody TaskUpdateStatusRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTaskStatus(
                id, request.getStatus(), request.getRemarks(),
                request.getAttachmentUrl(), authentication.getName(),
                request.getProgress()
        ));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<Task>> getMyTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksForUser(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String filter) {
        if (status != null || filter != null) {
            return ResponseEntity.ok(taskService.getFilteredTasks(status, filter));
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/{id}/updates")
    public ResponseEntity<List<TaskUpdate>> getTaskUpdates(@PathVariable String id) {
        return ResponseEntity.ok(taskService.getTaskUpdates(id));
    }
}
