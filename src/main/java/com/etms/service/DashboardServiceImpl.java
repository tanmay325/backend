package com.etms.service;

import com.etms.dto.DashboardStats;
import com.etms.entity.Role;
import com.etms.entity.TaskStatus;
import com.etms.repository.TaskRepository;
import com.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStats getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        long totalEmployees = userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && 
                       (user.getRole().name().equals("ROLE_EMPLOYEE") || user.getRole() == Role.ROLE_EMPLOYEE))
                .count();

        long tasksAssignedToday = taskRepository.countByCreatedAtAfter(startOfDay);

        long completedTasks = taskRepository.countByStatusIn(List.of(TaskStatus.COMPLETED));

        long pendingTasks = taskRepository.countByStatusIn(List.of(TaskStatus.ASSIGNED, TaskStatus.IN_PROGRESS));

        long overdueTasks = taskRepository.countByDueDateBeforeAndStatusNotIn(
                now, 
                List.of(TaskStatus.COMPLETED, TaskStatus.APPROVED)
        );
        
        long approvedTasks = taskRepository.countByStatusIn(List.of(TaskStatus.APPROVED));
        
        long rejectedTasks = taskRepository.countByStatusIn(List.of(TaskStatus.REJECTED));

        return DashboardStats.builder()
                .totalEmployees(totalEmployees)
                .tasksAssignedToday(tasksAssignedToday)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .overdueTasks(overdueTasks)
                .approvedTasks(approvedTasks)
                .rejectedTasks(rejectedTasks)
                .build();
    }
}
