package com.etms.service;

import com.etms.dto.TaskRequest;
import com.etms.entity.Task;
import com.etms.entity.TaskStatus;
import com.etms.entity.TaskUpdate;

import java.util.List;

public interface TaskService {
    Task createTask(TaskRequest request, String assignerEmail);
    Task updateTask(String taskId, TaskRequest request);
    void deleteTask(String taskId);
    Task updateTaskStatus(String taskId, TaskStatus status, String remarks, String attachmentUrl, String userEmail, int progress);
    List<Task> getTasksForUser(String email);
    List<Task> getAllTasks();
    List<Task> getFilteredTasks(String status, String filter);
    Task getTaskById(String taskId);
    List<TaskUpdate> getTaskUpdates(String taskId);
}
