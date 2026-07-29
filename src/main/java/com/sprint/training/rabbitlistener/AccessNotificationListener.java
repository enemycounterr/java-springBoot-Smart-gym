package com.sprint.training.rabbitlistener;

import com.sprint.training.config.RabbitMqConfig;
import com.sprint.training.constants.RabbitConstants;
import com.sprint.training.dto.rabbitevents.AccessRegisterEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AccessNotificationListener {

    @RabbitListener(queues = RabbitConstants.QUEUE_NOTIFICATIONS)
    public void handleAccessEvent(AccessRegisterEvent event) {
        System.out.println("=================================================");
        System.out.println("RCV EVENT FROM RABBITMQ ");
        System.out.println("Client: " + event.clientName() + " (ID: " + event.clientId() + ")");
        System.out.println("Zone: " + event.zoneName() + " | Direction: " + event.direction());
        System.out.println("Timestamp: " + event.timestamp());
        System.out.println("=================================================");
    }
}
