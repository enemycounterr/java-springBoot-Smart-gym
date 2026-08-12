package com.sprint.training.messaging.event;

import com.sprint.training.model.AccessDirection;

import java.time.Instant;

public record AccessRegisterEvent(
        Long logId,
        Long clientId,
        String clientName,
        String zoneName,
        AccessDirection direction,
        Instant timestamp
) { }
