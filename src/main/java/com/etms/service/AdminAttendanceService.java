package com.etms.service;

import com.etms.dto.AdminAttendanceResponse;
import java.time.LocalDate;
import java.util.List;

public interface AdminAttendanceService {
    List<AdminAttendanceResponse> getAttendanceByDate(LocalDate date);
    byte[] generateAttendancePdf(LocalDate date);
    byte[] generateAttendanceExcel(LocalDate startDate, LocalDate endDate);
}
