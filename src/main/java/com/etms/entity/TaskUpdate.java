package com.etms.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "task_updates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdate {

    @Id
    private String id;

    private String taskId;

    private String userId;

    private String remarks;

    private String attachmentUrl;

    private int progress;

    private LocalDateTime createdAt;
}
