package com.sprint.training.security.controller;

import com.sprint.training.security.dto.UpdateRoleRequest;
import com.sprint.training.security.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        this.authService.updateRole(userId, request);
        return ResponseEntity.noContent().build();
    }
}
