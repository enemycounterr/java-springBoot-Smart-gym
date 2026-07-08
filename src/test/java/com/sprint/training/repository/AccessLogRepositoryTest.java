package com.sprint.training.repository;


import com.sprint.training.model.AccessLog;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
public class AccessLogRepositoryTest {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccessZoneRepository accessZoneRepository;

    @Test
    public void findCurrentClientsInside_shouldReturnOnlyClientsWithLatestInDirection() {

        Client client = new Client(null, "Test User", "test@gym.com", true);
        clientRepository.save(client);

        AccessZone zone = new AccessZone(null, "VIP_ZONE");
        accessZoneRepository.save(zone);

        AccessLog log1 = new AccessLog("IN", client, zone);
        AccessLog log2 = new AccessLog("OUT", client, zone);
        AccessLog log3 = new AccessLog("IN", client, zone);

        log1.setTimeStamp(Instant.now().minus(30, ChronoUnit.MINUTES));
        log2.setTimeStamp(Instant.now().minus(10, ChronoUnit.MINUTES));
        log3.setTimeStamp(Instant.now());

        accessLogRepository.saveAll(List.of(log1, log2, log3));

        List<AccessLog> insideClients = accessLogRepository.findCurrentClientsInside();

        assertEquals(1, insideClients.size());
        assertEquals("VIP_ZONE", insideClients.get(0).getAccessZone().getZoneName());
        assertEquals("Test User", insideClients.get(0).getClient().getName());
    }
}
