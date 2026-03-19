package com.etms.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    private String id;

    private String title;

    private String description;

    private String assignedTo; // User ID
    private String assignedToName;

    private String assignedBy; // User ID
    private String assignedByName;

    private String category;

    private Priority priority;

    private TaskStatus status;

    private int progress;

    private LocalDateTime startDate;

    private LocalDateTime dueDate;

    private int estimatedTime;

    private LocalDateTime createdAt;
}
