package com.sprint.training.messaging.event;

import java.time.Instant;

public record AccessRegisterEvent(
        Long logId,
        Long clientId,
        String clientName,
        String zoneName,
        String direction,
        Instant timestamp
) { }
