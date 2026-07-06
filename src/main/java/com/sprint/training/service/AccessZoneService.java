package com.sprint.training.service;

import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.zone.AccessZoneCreateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.exceptions.ResourceNotFoundException;
import com.sprint.training.mapper.AccessZoneMapper;
import com.sprint.training.mapper.ClientMapper;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import com.sprint.training.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessZoneService {
    private final AccessZoneRepository zoneRepository;
    private final AccessLogRepository accessLogRepository;
    private final AccessZoneMapper mapper;

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public AccessZoneService(AccessZoneRepository repository, AccessLogRepository accessLogRepository, AccessZoneMapper mapper, ClientRepository clientRepository, ClientMapper clientMapper) {
        this.zoneRepository = repository;
        this.accessLogRepository = accessLogRepository;
        this.mapper = mapper;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public AccessZoneResponse createZone(AccessZoneCreateRequest request) {
        if (zoneRepository.findByZoneName(request.zoneName().toUpperCase()).isPresent()) {
            throw new IllegalArgumentException("Zone with name " + request.zoneName() + " already exists");
        }

        AccessZone zone = mapper.toEntity(request);
        return mapper.toDto(zoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<AccessZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteZone(Long zoneId) {
        AccessZone zone = this.zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with Id: " + zoneId));

        if (this.accessLogRepository.existsByAccessZoneId(zoneId)) {
            throw new IllegalStateException("Cannot delete zone; It contains logs in AccessLog repository");
        }

        for (Client client : List.copyOf(zone.getClients())) {
            client.removeAccessZone(zone);
        }

        zoneRepository.delete(zone);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getClientsByZone(Long zoneId) {
        if (!zoneRepository.existsById(zoneId)) {
            throw new ResourceNotFoundException("Zone not found with Id: " + zoneId);
        }

        return clientRepository.findAllByAccessZoneId(zoneId).stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
}
