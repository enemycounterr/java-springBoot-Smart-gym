package com.sprint.training.service;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.exceptions.ZoneAccessDeniedException;
import com.sprint.training.mapper.AccessMapper;
import com.sprint.training.mapper.ClientMapper;
import com.sprint.training.metrics.service.MetricsService;
import com.sprint.training.model.*;
import com.sprint.training.repository.AccessCardRepository;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import com.sprint.training.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessServiceTest {

    @Mock
    private AccessCardRepository accessCardRepository;
    @Mock
    private AccessLogRepository accessLogRepository;
    @Mock
    private AccessZoneRepository accessZoneRepository;
    @Mock
    private AccessMapper accessMapper;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private AccessService accessService;

    @Test
    public void registerAccess_whenDoubleIn_shouldThrowZoneAccessDeniedException() {

        String token = "RFID-123";
        Client client = new Client(1L, "Danek", "danek@mail.com", true);
        AccessCard card = new AccessCard(token, client);
        AccessZone zone = new AccessZone(1L, "GYM");
        client.addAccessZone(zone);

        AccessCheckRequest request = new AccessCheckRequest(token, 1L, AccessDirection.IN);

        AccessLog lastLog = new AccessLog(AccessDirection.IN, client, zone);

        when(accessCardRepository.findByRfidToken(token)).thenReturn(Optional.of(card));

        when(accessZoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        when(accessLogRepository.findFirstByClientIdOrderByTimeStampDesc(1L)).thenReturn(Optional.of(lastLog));

        assertThrows(ZoneAccessDeniedException.class, () -> accessService.registerAccess(request));
    }

    @Test
    public void registerAccess_whenDoubleOut_shouldThrowZoneAccessDeniedException() {
        String token = "RFID-123";
        Client client = new Client(1L, "Danek", "danek@mail.com", true);
        AccessCard card = new AccessCard(token, client);
        AccessZone zone = new AccessZone(1L, "GYM");
        client.addAccessZone(zone);

        AccessCheckRequest request = new AccessCheckRequest(token, 1L, AccessDirection.OUT);
        AccessLog lastLog = new AccessLog(AccessDirection.OUT, client, zone);

        when(accessCardRepository.findByRfidToken(token)).thenReturn(Optional.of(card));
        when(accessZoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(accessLogRepository.findFirstByClientIdOrderByTimeStampDesc(1L)).thenReturn(Optional.of(lastLog));

        assertThrows(ZoneAccessDeniedException.class, () -> accessService.registerAccess(request));
    }

    @Test
    public void registerAccess_whenInAfterOut_shouldSucceed() {
        String token = "RFID-123";
        Client client = new Client(1L, "Danek", "danek@mail.com", true);
        AccessCard card = new AccessCard(token, client);
        AccessZone zone = new AccessZone(1L, "GYM");
        client.addAccessZone(zone);

        AccessCheckRequest request = new AccessCheckRequest(token, 1L, AccessDirection.IN);
        AccessLog lastLog = new AccessLog(AccessDirection.OUT, client, zone);
        AccessLog savedLog = new AccessLog(AccessDirection.IN, client, zone);
        AccessLogResponse expectedResponse = new AccessLogResponse(
                1L,
                1L,
                "Danek",
                "GYM",
                AccessDirection.IN,
                savedLog.getTimeStamp()
        );

        when(accessCardRepository.findByRfidToken(token)).thenReturn(Optional.of(card));
        when(accessZoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(accessLogRepository.findFirstByClientIdOrderByTimeStampDesc(1L)).thenReturn(Optional.of(lastLog));
        when(accessMapper.toEntity(request, client, zone)).thenReturn(savedLog);
        when(accessLogRepository.save(savedLog)).thenReturn(savedLog);
        when(accessMapper.toDto(savedLog)).thenReturn(expectedResponse);
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("clientStats")).thenReturn(mockCache);

        AccessLogResponse result = accessService.registerAccess(request);

        assertEquals(AccessDirection.IN, result.direction());
    }

}
