package com.etms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminAttendanceResponse {
    private String userId;
    private String fullName;
    private String email;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // PRESENT, ABSENT
}
