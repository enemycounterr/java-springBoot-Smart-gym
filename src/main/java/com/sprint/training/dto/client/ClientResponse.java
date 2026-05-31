package com.sprint.training.dto.client;

public record ClientResponse(Long id, String name, String email, boolean isActive, String rfidToken){}