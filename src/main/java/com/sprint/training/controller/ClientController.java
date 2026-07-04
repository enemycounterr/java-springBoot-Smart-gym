package com.sprint.training.controller;


import com.sprint.training.dto.client.ClientCreateRequest;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.client.ClientUpdateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<ClientResponse> getAll(){
        return this.clientService.getAllClients();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ClientResponse getClientById(@PathVariable Long id){
        return this.clientService.getClientById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
        return this.clientService.updateClient(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponse changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return this.clientService.toggleClientStatus(id, active);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ClientResponse create(@Valid @RequestBody ClientCreateRequest request){
        return this.clientService.createClient(request);
    }

    @GetMapping("/{id}/zones")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public List<AccessZoneResponse> getClientZones(@PathVariable Long id) {
        return this.clientService.getClientZones(id);
    }


//    DONT NEED IT BECAUSE CLIENT COULD BE isActive=false AND AS A RESULT THE CLIENT WONT BE ABLE TO GO ANYWHERE
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id){
//        this.clientService.deleteClient(id);
//    }


}
