package com.etms.controller;

import com.etms.dto.UserRequest;
import com.etms.entity.OfficeLocation;
import com.etms.entity.User;
import com.etms.repository.OfficeLocationRepository;
import com.etms.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final OfficeLocationRepository officeLocationRepository;

    @PostMapping("/employees")
    public ResponseEntity<User> createEmployee(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminService.createEmployee(request));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<User>> getAllEmployees() {
        return ResponseEntity.ok(adminService.getAllEmployees());
    }

    @PostMapping("/location")
    public ResponseEntity<OfficeLocation> updateOfficeLocation(@RequestBody OfficeLocation location) {
        OfficeLocation existing = officeLocationRepository.findFirstByOrderByIdAsc().orElse(new OfficeLocation());
        existing.setLatitude(location.getLatitude());
        existing.setLongitude(location.getLongitude());
        existing.setRadius(location.getRadius() > 0 ? location.getRadius() : 100);
        return ResponseEntity.ok(officeLocationRepository.save(existing));
    }

    @GetMapping("/location")
    public ResponseEntity<OfficeLocation> getOfficeLocation() {
        return ResponseEntity.ok(officeLocationRepository.findFirstByOrderByIdAsc().orElse(new OfficeLocation()));
    }
}
