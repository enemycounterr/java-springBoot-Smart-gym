package com.sprint.training.service;

import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.zone.AccessZoneCreateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.dto.zone.AccessZoneUpdateRequest;
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
    private final AccessZoneMapper zoneMapper;

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public AccessZoneService(AccessZoneRepository repository, AccessLogRepository accessLogRepository, AccessZoneMapper mapper, ClientRepository clientRepository, ClientMapper clientMapper) {
        this.zoneRepository = repository;
        this.accessLogRepository = accessLogRepository;
        this.zoneMapper = mapper;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Transactional
    public AccessZoneResponse createZone(AccessZoneCreateRequest request) {
        if (zoneRepository.findByZoneName(request.zoneName().toUpperCase()).isPresent()) {
            throw new IllegalArgumentException("Zone with name " + request.zoneName() + " already exists");
        }

        AccessZone zone = zoneMapper.toEntity(request);
        return zoneMapper.toDto(zoneRepository.save(zone));
    }

    @Transactional
    public AccessZoneResponse renameZone(Long zoneId,AccessZoneUpdateRequest request){
        AccessZone zone = this.zoneRepository.findById(zoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Zone not found with Id: " + zoneId));

        String newName = request.zoneName().toUpperCase();

        if (this.zoneRepository.findByZoneName(newName).isPresent()){
            throw new IllegalArgumentException("Zone with name " + newName + " already exists");
        }

        zone.setZoneName(newName);
        return this.zoneMapper.toDto(zone);
    }

    @Transactional(readOnly = true)
    public List<AccessZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(zoneMapper::toDto)
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
