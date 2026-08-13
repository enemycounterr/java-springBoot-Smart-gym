package com.sprint.training.service;


import com.sprint.training.BaseIntegrationTest;
import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.ClientAccessStatsResponse;
import com.sprint.training.model.AccessCard;
import com.sprint.training.model.AccessDirection;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import com.sprint.training.repository.AccessCardRepository;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import com.sprint.training.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class AccessServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccessService accessService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccessZoneRepository accessZoneRepository;

    @Autowired
    private AccessCardRepository accessCardRepository;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Client savedClient;
    private AccessZone savedZone;
    private String rfidToken;

    @BeforeEach
    void setUp() {
        AccessZone zone = new AccessZone(null, "GYM_INTEGRATION_ZONE");
        savedZone = accessZoneRepository.save(zone);

        Client client = new Client(
                null,
                "Integration User",
                "integration@mail.com",
                true
        );
        client.setAccessZones(Set.of(savedZone));
        savedClient = clientRepository.save(client);

        rfidToken = "RFID-INT-999";
        AccessCard card = new AccessCard(rfidToken, savedClient);
        accessCardRepository.save(card);
    }

    @Test
    @DisplayName("Must cache statistics in Redis and invalidate the cache when passing through the turnstile")
    void getClientStats_shouldCacheInRedisAndEvictOnNewAccess() {
        Long clientId = savedClient.getId();

        ClientAccessStatsResponse stats1 = accessService.getClientStats(clientId);
        assertNotNull(stats1);
        assertEquals(0, stats1.totalEntries());

        String redisKey = "clientStats::" + clientId;
        Boolean hasKeyInRedis = redisTemplate.hasKey(redisKey);
        assertTrue(Boolean.TRUE.equals(hasKeyInRedis), "The key must be present in Redis after calling the method");

        AccessCheckRequest request = new AccessCheckRequest(rfidToken, savedZone.getId(), AccessDirection.IN);
        accessService.registerAccess(request);

        await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertEquals(Boolean.FALSE, redisTemplate.hasKey(redisKey), "The key exists after evict must be false");
                });

        ClientAccessStatsResponse stats2 = accessService.getClientStats(clientId);
        assertEquals(1, stats2.totalEntries(), "Statistics should update to 1 entry");
    }
}
