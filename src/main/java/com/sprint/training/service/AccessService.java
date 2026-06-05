package com.sprint.training.service;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.dto.access.ClientAccessStatsResponse;
import com.sprint.training.dto.access.ClientInsideResponse;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.exceptions.AccessDeniedException;
import com.sprint.training.exceptions.ClientAlreadyExistException;
import com.sprint.training.exceptions.ResourceNotFoundException;
import com.sprint.training.mapper.AccessMapper;
import com.sprint.training.mapper.ClientMapper;
import com.sprint.training.model.AccessCard;
import com.sprint.training.model.AccessLog;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import com.sprint.training.repository.AccessCardRepository;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import com.sprint.training.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessService {
    private final AccessLogRepository accessLogRepository;
    private final ClientRepository clientRepository;
    private final AccessZoneRepository accessZoneRepository;
    private final AccessCardRepository accessCardRepository;
    private final AccessMapper accessMapper;
    private final ClientMapper clientMapper;

    public AccessService(AccessLogRepository accessLogRepository, ClientRepository clientRepository, AccessZoneRepository accessZoneRepository, AccessCardRepository accessCardRepository, AccessMapper accessMapper, ClientMapper clientMapper) {
        this.accessLogRepository = accessLogRepository;
        this.clientRepository = clientRepository;
        this.accessZoneRepository = accessZoneRepository;
        this.accessCardRepository = accessCardRepository;
        this.accessMapper = accessMapper;
        this.clientMapper = clientMapper;
    }

    @Transactional(readOnly = true)
    public List<AccessLogResponse> getAllAccess() {
        return this.accessLogRepository.findAllWithClientAndZone().stream()
                .map(accessMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void grantAccessToZone(Long clientId, Long zoneId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        AccessZone zone = accessZoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + zoneId));

        if (client.getAccessZones().contains(zone)) {
            throw new ClientAlreadyExistException("Access already granted");
        }

        client.addAccessZone(zone);
        clientRepository.save(client);
    }

    @Transactional
    public void revokeAccessFromZone(Long clientId, Long zoneId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        AccessZone zone = accessZoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + zoneId));

        client.removeAccessZone(zone);
        clientRepository.save(client);
    }

    @Transactional
    public AccessLogResponse registerAccess(AccessCheckRequest request) {
        AccessCard card = accessCardRepository.findByRfidToken(request.rfidToken())
                .orElseThrow(() -> new ResourceNotFoundException("Access card not found with token:  " + request.rfidToken()));

        if (!card.isActive()) {
            throw new AccessDeniedException("Access denied! Card " + request.rfidToken() + " is inactive");
        }

        Client client = card.getClient();

        if (!client.isActive()) {
            throw new AccessDeniedException("Access denied! Client " + client.getName() + " is inactive!");
        }

        AccessZone accessZone = accessZoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + request.zoneId()));

        boolean hasAccess = client.getAccessZones().contains(accessZone);
        if (!hasAccess) {
            throw new AccessDeniedException("Access denied! Client '" + client.getName() +
                    "' does not have permission for zone '" + accessZone.getZoneName() + "'");
        }

        AccessLog log = accessMapper.toEntity(request, client, accessZone);
        AccessLog savedLog = this.accessLogRepository.save(log);

        return accessMapper.toDto(savedLog);
    }

    @Transactional(readOnly = true)
    public ClientAccessStatsResponse getClientStats(Long clientId) {
        Client client = this.clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        List<AccessLog> clientLogs = this.accessLogRepository.findAllByClientId(clientId);

        long inCount = clientLogs.stream()
                .filter(log -> "IN".equalsIgnoreCase(log.getDirection()))
                .count();

        long outCount = clientLogs.stream()
                .filter(log -> "OUT".equalsIgnoreCase(log.getDirection()))
                .count();

        return new ClientAccessStatsResponse(clientId, client.getName(), inCount, outCount);
    }

    @Transactional(readOnly = true)
    public List<ClientInsideResponse> getClientInside() {
        List<AccessLog> allLogs = this.accessLogRepository.findAllWithClientAndZone();

        Map<Client, Optional<AccessLog>> latestLogsPerClient = allLogs.stream()
                .collect(Collectors.groupingBy(
                        AccessLog::getClient,
                        Collectors.maxBy(Comparator.comparing(AccessLog::getTimeStamp))
                ));

        return latestLogsPerClient.entrySet().stream()
                .filter(entry -> entry.getValue().isPresent() && "IN".equalsIgnoreCase(entry.getValue().get().getDirection()))
                .map(entry -> {
                    Client client = entry.getKey();
                    AccessLog latestLog = entry.getValue().get();
                    return new ClientInsideResponse(
                            client.getId(),
                            client.getName(),
                            latestLog.getTimeStamp()
                    );
                })
                .sorted(Comparator.comparing(ClientInsideResponse::insideSince))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientByLogId(Long logId) {
        AccessLog log = this.accessLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Access log not found with Id: " + logId));

        return clientMapper.toDto(log.getClient());
    }


}
