package com.sprint.training.security.controller;

import com.sprint.training.security.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/users/{userId}/revoke")
    public ResponseEntity<Void> revokeUserTokens(@PathVariable Long userId) {
        this.authService.revokeAllUserTokens(userId);
        return ResponseEntity.noContent().build();
    }
}
