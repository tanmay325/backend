package com.etms.service;

import com.etms.dto.AdminAttendanceResponse;
import com.etms.entity.Attendance;
import com.etms.entity.Role;
import com.etms.entity.User;
import com.etms.repository.AttendanceRepository;
import com.etms.repository.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAttendanceServiceImpl implements AdminAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Override
    public List<AdminAttendanceResponse> getAttendanceByDate(LocalDate date) {
        List<User> employees = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_EMPLOYEE)
                .collect(Collectors.toList());

        return employees.stream().map(emp -> {
            Optional<Attendance> attendance = attendanceRepository.findByUserIdAndDate(emp.getId(), date);
            return AdminAttendanceResponse.builder()
                    .userId(emp.getId())
                    .fullName(emp.getFullName())
                    .email(emp.getEmail())
                    .date(date)
                    .checkInTime(attendance.map(Attendance::getCheckInTime).orElse(null))
                    .checkOutTime(attendance.map(Attendance::getCheckOutTime).orElse(null))
                    .status(attendance.isPresent() ? "PRESENT" : "ABSENT")
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generateAttendancePdf(LocalDate date) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("ETMS - Attendance Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Date: " + date.toString()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Employee Name");
            table.addCell("Email");
            table.addCell("Check In");
            table.addCell("Check Out");
            table.addCell("Status");

            List<AdminAttendanceResponse> data = getAttendanceByDate(date);
            for (AdminAttendanceResponse res : data) {
                table.addCell(res.getFullName());
                table.addCell(res.getEmail());
                table.addCell(res.getCheckInTime() != null ? res.getCheckInTime().toLocalTime().toString() : "--");
                table.addCell(res.getCheckOutTime() != null ? res.getCheckOutTime().toLocalTime().toString() : "--");
                table.addCell(res.getStatus());
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    public byte[] generateAttendanceExcel(LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            Row header = sheet.createRow(0);
            String[] columns = {"Name", "Email", "Date", "Check In", "Check Out", "Status"};
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                List<AdminAttendanceResponse> data = getAttendanceByDate(date);
                for (AdminAttendanceResponse res : data) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(res.getFullName());
                    row.createCell(1).setCellValue(res.getEmail());
                    row.createCell(2).setCellValue(res.getDate().toString());
                    row.createCell(3).setCellValue(res.getCheckInTime() != null ? res.getCheckInTime().toLocalTime().toString() : "--");
                    row.createCell(4).setCellValue(res.getCheckOutTime() != null ? res.getCheckOutTime().toLocalTime().toString() : "--");
                    row.createCell(5).setCellValue(res.getStatus());
                }
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}
