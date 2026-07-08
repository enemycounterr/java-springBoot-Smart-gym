package com.sprint.training.security.dto;

public record UserResponse(
        Long id,
        String userName,
        String email,
        String role
) {
}
