package com.sprint.training.dto.access;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AccessCheckRequest(
        @NotNull(message = "RFID token cannot be empty")
        String rfidToken,

        @NotNull(message = "Zone ID cannot be null")
        Long zoneId,

        @Pattern(regexp = "IN|OUT", message = "Direction must be either 'IN' or 'OUT'")
        String direction
) {
}
