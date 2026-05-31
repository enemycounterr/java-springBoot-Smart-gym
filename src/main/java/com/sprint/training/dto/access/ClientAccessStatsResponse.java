package com.sprint.training.dto.access;

public record ClientAccessStatsResponse(Long clientId, String clientName, long totalEntries, long totalExits) {
}
