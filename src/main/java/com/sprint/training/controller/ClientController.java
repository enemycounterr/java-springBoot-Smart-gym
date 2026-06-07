package com.sprint.training.controller;


import com.sprint.training.dto.client.ClientCreateRequest;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.client.ClientUpdateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.service.ClientService;
import jakarta.validation.Valid;
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
    public List<ClientResponse> getAll(){
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public ClientResponse getClientById(@PathVariable Long id){
        return this.clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.updateClient(id, request);
    }

    @PatchMapping("/{id}/status")
    public ClientResponse changeStatus(@PathVariable Long id, @RequestParam boolean active) {
        return clientService.toggleClientStatus(id, active);
    }


    @PostMapping
    public ClientResponse create(@Valid @RequestBody ClientCreateRequest request){
        return clientService.createClient(request);
    }

    @GetMapping("/{id}/zones")
    public List<AccessZoneResponse> getClientZones(@PathVariable Long id) {
        return clientService.getClientZones(id);
    }


//    DONT NEED IT BECAUSE CLIENT COULD BE isActive=false AND AS A RESULT THE CLIENT WONT BE ABLE TO GO ANYWHERE
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id){
//        this.clientService.deleteClient(id);
//    }


}
