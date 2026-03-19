package com.etms.dto;

import com.etms.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskUpdateStatusRequest {
    private TaskStatus status;
    private String remarks;
    private String attachmentUrl;
    private int progress;
}
