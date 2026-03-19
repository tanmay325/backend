package com.etms.dto;

import com.etms.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String id;
    private String email;
    private String fullName;
    private Role role;
}
