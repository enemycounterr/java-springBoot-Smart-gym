package com.sprint.training.security.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username or Email cant be empty")
        String username,

        @NotBlank(message = "Pasword cant be empty")
        String password
) {
}
