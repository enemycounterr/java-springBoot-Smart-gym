package com.sprint.training.messaging.controller;

import com.sprint.training.messaging.service.RabbitAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/rabbitmq")
public class RabbitAdminController {
    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reprocess-dlq")
    public ResponseEntity<Map<String, Object>> reprocessDlq() {
        int count = rabbitAdminService.reprocessDlqMessages();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Messages successfully re-queued for processing",
                "count", count
        ));
    }
}
