package com.sprint.training.dto.access;

import java.time.LocalDateTime;

public record ClientInsideResponse(
        Long clientId,
        String clientName,
        LocalDateTime insideSince
) {
}
