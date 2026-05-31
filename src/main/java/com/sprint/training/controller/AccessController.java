package com.sprint.training.controller;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.dto.access.ClientAccessStatsResponse;
import com.sprint.training.dto.access.ClientInsideResponse;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.service.AccessService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/access")
public class AccessController {
    private final AccessService accessService;

    public AccessController(AccessService accessService) {
        this.accessService = accessService;
    }


    @GetMapping()
    public List<AccessLogResponse> getAll(){
        return this.accessService.getAllAccess();
    }

    @PostMapping("/register")
    public AccessLogResponse register(@Valid @RequestBody AccessCheckRequest request){
        return this.accessService.registerAccess(request);
    }

    @GetMapping("/clients/{clientId}/stats")
    public ClientAccessStatsResponse getStats(@PathVariable Long clientId){
        return this.accessService.getClientStats(clientId);
    }

    @GetMapping("/inside")
    public List<ClientInsideResponse> getClientInside(){
        return this.accessService.getClientInside();
    }

    @PostMapping("/clients/{clientId}/zones/{zoneId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void grantAccess(@PathVariable Long clientId, @PathVariable Long zoneId) {
        this.accessService.grantAccessToZone(clientId, zoneId);
    }

    @GetMapping("logs/{logId}/client")
    public ClientResponse getClientByLogId(@PathVariable Long logId){
        return this.accessService.getClientByLogId(logId);
    }
}
