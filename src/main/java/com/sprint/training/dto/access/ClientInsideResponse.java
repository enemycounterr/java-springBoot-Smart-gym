package com.sprint.training.dto.access;

import java.time.Instant;
import java.time.LocalDateTime;

public record ClientInsideResponse(
        Long clientId,
        String clientName,
        Instant insideSince
) {
}
