package com.sprint.training.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "Username cannot be empty")
        String username,

        @NotBlank(message = "Password cannot be empty")
        String password,

        @NotBlank(message = "Role cannot be empty (ADMIN or GUARD)")
        String role,

        @Email(message = "Invalid email type")
        String email
) {
}
