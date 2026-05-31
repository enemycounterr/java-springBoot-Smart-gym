package com.sprint.training.controller;


import com.sprint.training.dto.zone.AccessZoneCreateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.service.AccessZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zones")
public class AccessZoneController {
    private final AccessZoneService zoneService;

    public AccessZoneController(AccessZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccessZoneResponse create(@Valid @RequestBody AccessZoneCreateRequest request){
        return this.zoneService.createZone(request);
    }

    @GetMapping
    public List<AccessZoneResponse> getAll() {
        return zoneService.getAllZones();
    }

}
