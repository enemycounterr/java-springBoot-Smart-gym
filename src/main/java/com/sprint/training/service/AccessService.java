package com.sprint.training.service;


import com.sprint.training.config.RabbitMqConfig;
import com.sprint.training.constants.RabbitConstants;
import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.dto.access.ClientAccessStatsResponse;
import com.sprint.training.dto.access.ClientInsideResponse;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.dto.rabbitevents.AccessRegisterEvent;
import com.sprint.training.exceptions.ClientAlreadyExistException;
import com.sprint.training.exceptions.ResourceNotFoundException;
import com.sprint.training.exceptions.ZoneAccessDeniedException;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
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
    private final CacheManager cacheManager;
    private final RabbitTemplate rabbitTemplate;

    public AccessService(AccessLogRepository accessLogRepository, ClientRepository clientRepository, AccessZoneRepository accessZoneRepository, AccessCardRepository accessCardRepository, AccessMapper accessMapper, ClientMapper clientMapper, CacheManager cacheManager, RabbitTemplate rabbitTemplate) {
        this.accessLogRepository = accessLogRepository;
        this.clientRepository = clientRepository;
        this.accessZoneRepository = accessZoneRepository;
        this.accessCardRepository = accessCardRepository;
        this.accessMapper = accessMapper;
        this.clientMapper = clientMapper;
        this.cacheManager = cacheManager;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public Page<AccessLogResponse> getAllLogs(Pageable pageable) {
        Page<AccessLog> logsPage = this.accessLogRepository.findAll(pageable);

        return logsPage.map(this.accessMapper::toDto);
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

//    @CacheEvict(value = "clientStats", key = "#result.clientId")
    @Transactional
    public AccessLogResponse registerAccess(AccessCheckRequest request) {
        AccessCard card = accessCardRepository.findByRfidToken(request.rfidToken())
                .orElseThrow(() -> new ResourceNotFoundException("Access card not found with token:  " + request.rfidToken()));

        Client client = card.getClient();

        if (!client.isActive()) {
            throw new ZoneAccessDeniedException("Access denied! Client " + client.getName() + " is inactive!");
        }

        AccessZone accessZone = accessZoneRepository.findById(request.zoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with ID: " + request.zoneId()));

        boolean hasAccess = client.getAccessZones().contains(accessZone);
        if (!hasAccess) {
            throw new ZoneAccessDeniedException("Access denied! Client '" + client.getName() +
                    "' does not have permission for zone '" + accessZone.getZoneName() + "'");
        }

        Optional<AccessLog> latestLog = this.accessLogRepository.findFirstByClientIdOrderByTimeStampDesc(client.getId());
        if (latestLog.isPresent() && latestLog.get().getDirection().equalsIgnoreCase(request.direction())) {
            throw new ZoneAccessDeniedException("Anti-Passback violation! Client '" + client.getName() +
                    "' already performed direction: " + request.direction());
        }

        AccessLog log = accessMapper.toEntity(request, client, accessZone);
        AccessLog savedLog = this.accessLogRepository.save(log);

        AccessRegisterEvent event = new AccessRegisterEvent(
                savedLog.getId(),
                client.getId(),
                client.getName(),
                accessZone.getZoneName(),
                savedLog.getDirection(),
                savedLog.getTimeStamp()
        );

        this.rabbitTemplate.convertAndSend(
                RabbitConstants.EXCHANGE_GYM,
                RabbitConstants.ROUTING_KEY_ACCESS_REGISTERED,
                event
        );

        evictClientStatsCache(savedLog.getClient().getId());

        return accessMapper.toDto(savedLog);
    }

    private void evictClientStatsCache(Long clientId) {
        this.cacheManager.getCache("clientStats").evict(clientId);
    }

    @Cacheable(value = "clientStats", key = "#clientId")
    @Transactional(readOnly = true)
    public ClientAccessStatsResponse getClientStats(Long clientId) {
        System.out.println("-----------------METHOD INVOKE getClientStats ------------------------");
        Client client = this.clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));

        long inCount = this.accessLogRepository.countByClientIdAndDirectionIgnoreCase(clientId, "IN");

        long outCount = this.accessLogRepository.countByClientIdAndDirectionIgnoreCase(clientId, "OUT");

        return new ClientAccessStatsResponse(clientId, client.getName(), inCount, outCount);
    }

    @Transactional(readOnly = true)
    public List<ClientInsideResponse> getClientInside() {
        List<AccessLog> insideLogs = this.accessLogRepository.findCurrentClientsInside();

        return insideLogs.stream()
                .map(log -> new ClientInsideResponse(
                        log.getClient().getId(),
                        log.getClient().getName(),
                        log.getTimeStamp()
                ))
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
