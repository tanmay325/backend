package com.etms;

import com.etms.entity.Role;
import com.etms.entity.User;
import com.etms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class EtmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EtmsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail("admin@etms.com").isEmpty()) {
				User admin = User.builder()
						.email("admin@etms.com")
						.password(passwordEncoder.encode("admin123"))
						.fullName("System Admin")
						.role(Role.ROLE_ADMIN)
						.createdAt(LocalDateTime.now())
						.build();
				userRepository.save(admin);
				System.out.println("Admin user created: admin@etms.com / admin123");
			}
		};
	}
}
