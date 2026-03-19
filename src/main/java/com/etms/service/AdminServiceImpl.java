package com.etms.service;

import com.etms.dto.UserRequest;
import com.etms.entity.Role;
import com.etms.entity.User;
import com.etms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createEmployee(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName()) 
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && 
                       (user.getRole().name().equals("ROLE_EMPLOYEE") || user.getRole() == Role.ROLE_EMPLOYEE))
                .collect(Collectors.toList());
    }
}
