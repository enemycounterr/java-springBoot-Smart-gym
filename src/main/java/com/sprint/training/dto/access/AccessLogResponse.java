package com.sprint.training.dto.access;

import com.sprint.training.model.AccessDirection;

import java.time.Instant;

public record AccessLogResponse(
        Long logId,
        Long clientId,
        String clientName,
        String zoneName,
        AccessDirection direction,
        Instant timestamp
) {
}
