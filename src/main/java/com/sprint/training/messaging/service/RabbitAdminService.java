package com.sprint.training.messaging.service;


import com.sprint.training.constants.RabbitConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
public class RabbitAdminService {
    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitAdminService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public int reprocessDlqMessages() {
        int reprocessedCount = 0;
        Message message;

        while ((message = rabbitTemplate.receive(RabbitConstants.QUEUE_NOTIFICATIONS_DLQ)) != null) {

            rabbitTemplate.send(
                    RabbitConstants.EXCHANGE_GYM,
                    RabbitConstants.ROUTING_KEY_ACCESS_REGISTERED,
                    message
            );
            reprocessedCount++;
        }

        log.info("Reprocessed {} messages from DLQ", reprocessedCount);
        return reprocessedCount;
    }
}
