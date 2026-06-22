package com.sprint.training.service;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.exceptions.ZoneAccessDeniedException;
import com.sprint.training.model.AccessCard;
import com.sprint.training.model.AccessLog;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import com.sprint.training.repository.AccessCardRepository;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessServiceTest {

    @Mock
    private AccessCardRepository accessCardRepository;
    @Mock
    private AccessLogRepository accessLogRepository;
    @Mock
    private AccessZoneRepository accessZoneRepository;

    @InjectMocks
    private AccessService accessService;

    @Test
    public void registerAccess_whenDoubleIn_shouldThrowZoneAccessDeniedException() {

        String token = "RFID-123";
        Client client = new Client(1L, "Danek", "danek@mail.com", true);
        AccessCard card = new AccessCard(token, client);
        AccessZone zone = new AccessZone(1L, "GYM");
        client.addAccessZone(zone);

        AccessCheckRequest request = new AccessCheckRequest(token, 1L, "IN");

        AccessLog lastLog = new AccessLog("IN", client, zone);

        when(accessCardRepository.findByRfidToken(token)).thenReturn(Optional.of(card));

        when(accessZoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        when(accessLogRepository.findFirstByClientIdOrderByTimeStampDesc(1L)).thenReturn(Optional.of(lastLog));

        assertThrows(ZoneAccessDeniedException.class, () -> {
            accessService.registerAccess(request);
        });
    }

}
