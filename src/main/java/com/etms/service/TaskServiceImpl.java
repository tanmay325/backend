package com.etms.service;

import com.etms.dto.TaskRequest;
import com.etms.entity.*;
import com.etms.repository.TaskRepository;
import com.etms.repository.TaskUpdateRepository;
import com.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskUpdateRepository taskUpdateRepository;
    private final NotificationService notificationService;

    @Override
    public Task createTask(TaskRequest request, String assignerEmail) {
        User assigner = userRepository.findByEmail(assignerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Assigner not found"));
        
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.ASSIGNED)
                .assignedBy(assigner.getId())
                .assignedByName(assigner.getFullName())
                .assignedTo(assignee.getId())
                .assignedToName(assignee.getFullName())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .estimatedTime(request.getEstimatedTime())
                .progress(0)
                .createdAt(LocalDateTime.now())
                .build();

        Task savedTask = taskRepository.save(task);
        
        if (savedTask.getStatus() == TaskStatus.ASSIGNED) {
            notificationService.sendNotification(
                assignee.getId(), 
                "New Task Assigned", 
                "You have been assigned a new task: " + savedTask.getTitle(),
                "TASK_ASSIGNED",
                savedTask.getId()
            );
        }

        return savedTask;
    }

    @Override
    public Task updateTask(String taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setAssignedTo(assignee.getId());
        task.setAssignedToName(assignee.getFullName());
        task.setCategory(request.getCategory());
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setEstimatedTime(request.getEstimatedTime());
        
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(String taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public Task updateTaskStatus(String taskId, TaskStatus status, String remarks, String attachmentUrl, String userEmail, int progress) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        task.setStatus(status);
        task.setProgress(progress);
        
        TaskUpdate update = TaskUpdate.builder()
                .taskId(task.getId())
                .userId(user.getId())
                .remarks(remarks)
                .progress(progress)
                .attachmentUrl(attachmentUrl)
                .createdAt(LocalDateTime.now())
                .build();
        
        taskUpdateRepository.save(update);
        Task savedTask = taskRepository.save(task);

        if (status == TaskStatus.COMPLETED) {
            notificationService.sendNotification(task.getAssignedBy(), "Task Completed", 
                user.getFullName() + " has completed the task: " + task.getTitle(), "TASK_COMPLETED", task.getId());
        } else if (status == TaskStatus.APPROVED) {
            notificationService.sendNotification(task.getAssignedTo(), "Task Approved", 
                "Your task '" + task.getTitle() + "' has been approved.", "TASK_APPROVED", task.getId());
        } else if (status == TaskStatus.REJECTED) {
            notificationService.sendNotification(task.getAssignedTo(), "Task Rejected", 
                "Your task '" + task.getTitle() + "' requires rework.", "TASK_REJECTED", task.getId());
        }

        return savedTask;
    }

    @Override
    public List<TaskUpdate> getTaskUpdates(String taskId) {
        return taskUpdateRepository.findByTaskId(taskId);
    }

    @Override
    public List<Task> getTasksForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findByAssignedTo(user.getId());
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getFilteredTasks(String status, String filter) {
        List<Task> tasks = taskRepository.findAll();
        
        if (status != null && !status.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus().name().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        
        if (filter != null && !filter.isEmpty()) {
            tasks = tasks.stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return tasks;
    }

    @Override
    public Task getTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
