package com.etms.service;

import com.etms.entity.Role;
import com.etms.entity.Task;
import com.etms.entity.TaskStatus;
import com.etms.entity.User;
import com.etms.repository.TaskRepository;
import com.etms.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getDailyReport() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        List<Task> allTasks = taskRepository.findAll();

        long assignedToday = allTasks.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(startOfDay))
                .count();

        long completedToday = allTasks.stream()
                .filter(t -> (t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.APPROVED))
                .count();

        Map<String, Object> report = new HashMap<>();
        report.put("date", LocalDateTime.now().toLocalDate().toString());
        report.put("assignedToday", assignedToday);
        report.put("completedToday", completedToday);
        return report;
    }

    @Override
    public Map<String, Object> getEmployeePerformance() {
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_EMPLOYEE)
                .collect(Collectors.toList());

        List<Task> allTasks = taskRepository.findAll();

        Map<String, Object> performance = new HashMap<>();
        for (User emp : employees) {
            long completed = allTasks.stream()
                    .filter(t -> emp.getId().equals(t.getAssignedTo()) && 
                            (t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.APPROVED))
                    .count();
            performance.put(emp.getFullName(), completed);
        }
        return performance;
    }

    @Override
    public byte[] generatePdfReport() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph para = new Paragraph("ETMS - Productivity Report", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.addCell("Task Title");
            table.addCell("Assigned To");
            table.addCell("Status");
            table.addCell("Progress");

            List<Task> tasks = taskRepository.findAll();
            for (Task task : tasks) {
                table.addCell(task.getTitle());
                String empName = userRepository.findById(task.getAssignedTo())
                        .map(User::getFullName).orElse("Unknown");
                table.addCell(empName);
                table.addCell(task.getStatus().toString());
                table.addCell(task.getProgress() + "%");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
