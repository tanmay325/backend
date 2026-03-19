package com.etms.repository;

import com.etms.entity.TaskUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskUpdateRepository extends MongoRepository<TaskUpdate, String> {
    List<TaskUpdate> findByTaskId(String taskId);
    void deleteByTaskId(String taskId);
}
