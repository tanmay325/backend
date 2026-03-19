package com.etms.repository;

import com.etms.entity.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {
    Optional<Attendance> findByUserIdAndDate(String userId, LocalDate date);
    List<Attendance> findByUserId(String userId);
}
