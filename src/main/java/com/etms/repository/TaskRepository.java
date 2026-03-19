package com.etms.repository;

import com.etms.entity.Task;
import com.etms.entity.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByAssignedTo(String assignedTo);
    List<Task> findByAssignedBy(String assignedBy);
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByStatusIn(List<TaskStatus> statuses);
    long countByDueDateBeforeAndStatusNotIn(LocalDateTime dateTime, List<TaskStatus> statuses);
}
