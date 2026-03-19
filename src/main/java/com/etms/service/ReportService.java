package com.etms.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> getDailyReport();
    Map<String, Object> getEmployeePerformance();
    byte[] generatePdfReport();
}
