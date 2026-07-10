package com.sprint.training.controller;


import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.zone.AccessZoneCreateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.dto.zone.AccessZoneUpdateRequest;
import com.sprint.training.service.AccessZoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public AccessZoneResponse create(@Valid @RequestBody AccessZoneCreateRequest request) {
        return this.zoneService.createZone(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AccessZoneResponse rename(@PathVariable Long id,
                                     @Valid @RequestBody AccessZoneUpdateRequest request) {
        return this.zoneService.renameZone(id, request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<AccessZoneResponse> getAll() {
        return this.zoneService.getAllZones();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        this.zoneService.deleteZone(id);
    }

    @GetMapping("/{id}/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<ClientResponse> getClientsByZone(@PathVariable Long id) {
        return this.zoneService.getClientsByZone(id);
    }

}
