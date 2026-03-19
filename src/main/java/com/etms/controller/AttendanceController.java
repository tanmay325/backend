package com.etms.controller;

import com.etms.dto.AttendanceResponse;
import com.etms.entity.Attendance;
import com.etms.entity.OfficeLocation;
import com.etms.entity.User;
import com.etms.repository.AttendanceRepository;
import com.etms.repository.OfficeLocationRepository;
import com.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final OfficeLocationRepository officeLocationRepository;

    @GetMapping("/today")
    public ResponseEntity<AttendanceResponse> getTodayAttendance(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        return userRepository.findByEmail(authentication.getName())
                .map(user -> {
                    Optional<Attendance> attendance = attendanceRepository.findByUserIdAndDate(user.getId(), LocalDate.now());
                    return ResponseEntity.ok(AttendanceResponse.builder()
                            .isCheckedIn(attendance.isPresent())
                            .checkInTime(attendance.map(Attendance::getCheckInTime).orElse(null))
                            .checkOutTime(attendance.map(Attendance::getCheckOutTime).orElse(null))
                            .id(attendance.map(Attendance::getId).orElse(null))
                            .build());
                })
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestBody Attendance request, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> {
                    if (attendanceRepository.findByUserIdAndDate(user.getId(), LocalDate.now()).isPresent()) {
                        return ResponseEntity.badRequest().body("Already checked in for today");
                    }

                    OfficeLocation office = officeLocationRepository.findFirstByOrderByIdAsc().orElse(null);
                    if (office != null) {
                        double distance = calculateDistance(request.getLatitude(), request.getLongitude(), 
                                                           office.getLatitude(), office.getLongitude());
                        if (distance > office.getRadius()) {
                            return ResponseEntity.badRequest().body("You are outside office location. Distance: " + (int)distance + "m");
                        }
                    }

                    Attendance attendance = Attendance.builder()
                            .userId(user.getId())
                            .checkInTime(LocalDateTime.now())
                            .date(LocalDate.now())
                            .latitude(request.getLatitude())
                            .longitude(request.getLongitude())
                            .checkInLocation(request.getCheckInLocation())
                            .build();

                    return ResponseEntity.ok(attendanceRepository.save(attendance));
                })
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(@RequestBody Attendance request, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> {
                    Attendance attendance = attendanceRepository.findByUserIdAndDate(user.getId(), LocalDate.now()).orElse(null);
                    if (attendance == null) return ResponseEntity.badRequest().body("No check-in record found for today");

                    OfficeLocation office = officeLocationRepository.findFirstByOrderByIdAsc().orElse(null);
                    if (office != null) {
                        double distance = calculateDistance(request.getLatitude(), request.getLongitude(), 
                                                           office.getLatitude(), office.getLongitude());
                        if (distance > office.getRadius()) {
                            return ResponseEntity.badRequest().body("You are outside office location. Distance: " + (int)distance + "m");
                        }
                    }

                    attendance.setCheckOutTime(LocalDateTime.now());
                    attendance.setCheckOutLocation(request.getCheckOutLocation());
                    return ResponseEntity.ok(attendanceRepository.save(attendance));
                })
                .orElse(ResponseEntity.status(404).build());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }
}
