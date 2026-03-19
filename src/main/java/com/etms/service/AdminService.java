package com.etms.service;

import com.etms.dto.UserRequest;
import com.etms.entity.User;

import java.util.List;

public interface AdminService {
    User createEmployee(UserRequest request);
    List<User> getAllEmployees();
}
