package com.etms.repository;

import com.etms.entity.Role;
import com.etms.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
}
