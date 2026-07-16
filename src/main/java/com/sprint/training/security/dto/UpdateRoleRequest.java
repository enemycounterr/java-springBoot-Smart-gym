package com.sprint.training.security.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(
        @NotBlank(message = "Role cant be empty")
        String role
) {
}
