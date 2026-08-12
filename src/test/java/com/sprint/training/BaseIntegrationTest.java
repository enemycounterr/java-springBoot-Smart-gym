package com.sprint.training;

import com.sprint.training.configuration.IntegrationContainersConfiguration;
import com.sprint.training.integration.CrmClient;
import com.sprint.training.repository.AccessCardRepository;
import com.sprint.training.repository.AccessLogRepository;
import com.sprint.training.repository.AccessZoneRepository;
import com.sprint.training.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IntegrationContainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected AccessLogRepository accessLogRepository;

    @Autowired
    protected AccessCardRepository accessCardRepository;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected AccessZoneRepository accessZoneRepository;

    @Autowired
    protected StringRedisTemplate redisTemplate;

    @MockitoBean
    protected CrmClient crmClient;

    @BeforeEach
    void cleanDatabase() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });

        accessLogRepository.deleteAll();
        accessCardRepository.deleteAll();
        clientRepository.deleteAll();
        accessZoneRepository.deleteAll();
    }

}
