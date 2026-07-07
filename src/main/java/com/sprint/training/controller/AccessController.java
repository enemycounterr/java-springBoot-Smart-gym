package com.sprint.training.controller;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.dto.access.ClientAccessStatsResponse;
import com.sprint.training.dto.access.ClientInsideResponse;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.service.AccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/access")
public class AccessController {
    private final AccessService accessService;

    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<AccessLogResponse> getAll(){
        return this.accessService.getAllAccess();
    }

    @PostMapping("/clients/{clientId}/zones/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void grantAccess(@PathVariable Long clientId, @PathVariable Long zoneId) {
        this.accessService.grantAccessToZone(clientId, zoneId);
    }

    @DeleteMapping("/clients/{clientId}/zones/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void revokeAccess(@PathVariable Long clientId, @PathVariable Long zoneId) {
        this.accessService.revokeAccessFromZone(clientId, zoneId);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public AccessLogResponse register(@Valid @RequestBody AccessCheckRequest request){
        return this.accessService.registerAccess(request);
    }

    @GetMapping("/clients/{clientId}/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ClientAccessStatsResponse getStats(@PathVariable Long clientId){
        return this.accessService.getClientStats(clientId);
    }

    @GetMapping("/inside")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<ClientInsideResponse> getClientInside(){
        return this.accessService.getClientInside();
    }

    @GetMapping("logs/{logId}/client")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ClientResponse getClientByLogId(@PathVariable Long logId){
        return this.accessService.getClientByLogId(logId);
    }
}
