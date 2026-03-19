package com.etms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;
    private String userId;
    private String title;
    private String message;
    private boolean read;
    private String type; // TASK_ASSIGNED, TASK_REJECTED, TASK_APPROVED, DEADLINE_REMINDER
    private String relatedId; // taskId
    private LocalDateTime createdAt;
}
