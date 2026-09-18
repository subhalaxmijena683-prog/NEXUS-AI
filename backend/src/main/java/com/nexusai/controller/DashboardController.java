package com.nexusai.controller;

import com.nexusai.dto.DashboardResponse;
import com.nexusai.entity.User;
import com.nexusai.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardResponse response = new DashboardResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                0,
                0,
                0.0
        );

        return ResponseEntity.ok(response);
    }
}