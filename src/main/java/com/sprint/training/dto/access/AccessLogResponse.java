package com.sprint.training.dto.access;

import java.time.Instant;

public record AccessLogResponse(
        Long logId,
        Long clientId,
        String clientName,
        String zoneName,
        String direction,
        Instant timestamp
) {
}
