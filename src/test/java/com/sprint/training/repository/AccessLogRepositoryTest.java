package com.sprint.training.repository;


import com.sprint.training.BaseIntegrationTest;
import com.sprint.training.model.AccessDirection;
import com.sprint.training.model.AccessLog;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccessLogRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccessZoneRepository accessZoneRepository;

    @Test
    @Transactional
    public void findCurrentClientsInside_shouldReturnOnlyClientsWithLatestInDirection() {

        Client client = new Client(null, "Test User", "test@gym.com", true);
        clientRepository.save(client);

        AccessZone zone = new AccessZone(null, "VIP_ZONE");
        accessZoneRepository.save(zone);

        AccessLog log1 = new AccessLog(AccessDirection.IN, client, zone);
        AccessLog log2 = new AccessLog(AccessDirection.OUT, client, zone);
        AccessLog log3 = new AccessLog(AccessDirection.IN, client, zone);

        log1.setTimeStamp(Instant.now().minus(30, ChronoUnit.MINUTES));
        log2.setTimeStamp(Instant.now().minus(10, ChronoUnit.MINUTES));
        log3.setTimeStamp(Instant.now());

        accessLogRepository.saveAll(List.of(log1, log2, log3));

        List<AccessLog> insideClients = accessLogRepository.findCurrentClientsInside(AccessDirection.IN);

        assertEquals(1, insideClients.size());
        assertEquals("VIP_ZONE", insideClients.get(0).getAccessZone().getZoneName());
        assertEquals("Test User", insideClients.get(0).getClient().getName());
    }

    @Test
    @Transactional
    public void countByClientIdAndDirection_shouldCountCorrectlyByEnum() {
        Client client = new Client(null, "Count User", "count@gym.com", true);
        clientRepository.save(client);

        AccessZone zone = new AccessZone(null, "COUNT_ZONE");
        accessZoneRepository.save(zone);

        AccessLog log1 = new AccessLog(AccessDirection.IN, client, zone);
        AccessLog log2 = new AccessLog(AccessDirection.OUT, client, zone);
        AccessLog log3 = new AccessLog(AccessDirection.IN, client, zone);
        accessLogRepository.saveAll(List.of(log1, log2, log3));

        long inCount = accessLogRepository.countByClientIdAndDirection(client.getId(), AccessDirection.IN);
        long outCount = accessLogRepository.countByClientIdAndDirection(client.getId(), AccessDirection.OUT);

        assertEquals(2, inCount);
        assertEquals(1, outCount);
    }
}
