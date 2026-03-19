package com.etms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private long totalEmployees;
    private long tasksAssignedToday;
    private long completedTasks;
    private long pendingTasks;
    private long overdueTasks;
    private long approvedTasks;
    private long rejectedTasks;
}
