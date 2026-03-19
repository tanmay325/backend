package com.etms.dto;

import com.etms.entity.Priority;
import com.etms.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Priority priority;
    @NotNull
    private String assigneeId;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private int estimatedTime;
    private String category;
    private TaskStatus status;
}
