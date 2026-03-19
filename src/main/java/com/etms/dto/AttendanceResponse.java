package com.etms.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponse {
    private String id;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private boolean isCheckedIn;
}
