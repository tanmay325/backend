package com.etms.controller;

import com.etms.entity.User;
import com.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@RequestBody User profileData, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> {
                    user.setFullName(profileData.getFullName());
                    user.setPhoneNumber(profileData.getPhoneNumber());
                    user.setDepartment(profileData.getDepartment());
                    user.setDesignation(profileData.getDesignation());
                    user.setProfilePictureUrl(profileData.getProfilePictureUrl());
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
