package com.sprint.training.service;


import com.sprint.training.dto.client.ClientCreateRequest;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.client.ClientUpdateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.exceptions.ClientAlreadyExistException;
import com.sprint.training.exceptions.ResourceNotFoundException;
import com.sprint.training.mapper.AccessZoneMapper;
import com.sprint.training.mapper.ClientMapper;
import com.sprint.training.model.AccessCard;
import com.sprint.training.model.Client;
import com.sprint.training.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final AccessZoneMapper zoneMapper;

    public ClientService(ClientRepository repository, ClientMapper clientMapper, AccessZoneMapper zoneMapper) {
        this.clientRepository = repository;
        this.clientMapper = clientMapper;
        this.zoneMapper = zoneMapper;
    }

    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        if (clientRepository.existsByEmail(request.email())) {
            throw new ClientAlreadyExistException("Client with email " + request.email() + " already exist");
        }

        Client newClient = clientMapper.toEntity(request);
        String generatedToken = "RFID-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        AccessCard card = new AccessCard(generatedToken, newClient);
        newClient.setAccessCard(card);

        Client savedClient = clientRepository.save(newClient);

        return clientMapper.toDto(savedClient);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {

        return this.clientRepository.findAllWithAccessCard().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));
        return clientMapper.toDto(client);
    }

    @Transactional
    public ClientResponse updateClient(Long id, ClientUpdateRequest request) {
        Client client = this.clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        if (request.name() != null) {
            client.setName(request.name());
        }

        if (request.email() != null) {
            if (!client.getEmail().equalsIgnoreCase(request.email()) && clientRepository.existsByEmail(request.email())) {
                throw new ClientAlreadyExistException("Email " + request.email() + " is already taken");
            }
            client.setEmail(request.email());
        }

        Client updatedClient = this.clientRepository.save(client);

        return clientMapper.toDto(updatedClient);

    }

    @Transactional
    public ClientResponse toggleClientStatus(Long id, boolean isActive) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        client.setActive(isActive);
        if (client.getAccessCard() != null) {
            client.getAccessCard().setActive(isActive);
        }

        Client updatedClient = clientRepository.save(client);
        return clientMapper.toDto(updatedClient);
    }

    @Transactional(readOnly = true)
    public List<AccessZoneResponse> getClientZones(Long clientId) {
        Client client = clientRepository.findByIdWithZones(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        return client.getAccessZones().stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }


    /*

    DONT NEED IT BECAUSE CLIENT COULD BE isActive=false AND AS A RESULT THE CLIENT WONT BE ABLE TO GO ANYWHERE
        @Transactional
        public void deleteClient(Long id){
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        clientRepository.delete(client);
    }
     */


}
