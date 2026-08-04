package com.sprint.training.messaging.listener;

import com.sprint.training.constants.RabbitConstants;
import com.sprint.training.messaging.event.AccessRegisterEvent;
import com.sprint.training.exceptions.CrmIntegrationException;
import com.sprint.training.integration.CrmClient;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AccessNotificationListener {

    private final CrmClient crmClient;

    public AccessNotificationListener(CrmClient crmClient) {
        this.crmClient = crmClient;
    }

    @RabbitListener(queues = RabbitConstants.QUEUE_NOTIFICATIONS)
    public void handleAccessEvent(AccessRegisterEvent event) {
        System.out.println("=================================================");
        System.out.println(" RCV EVENT FROM RABBITMQ ");
        System.out.println("Client: " + event.clientName() + " (ID: " + event.clientId() + ")");

        if ("IN".equalsIgnoreCase(event.direction())) {
            try {
                System.out.println("Sending data to external CRM...");
                this.crmClient.sendLoyaltyPoints(event.clientId(), event.clientName());
                System.out.println("CRM successfully updated!");
            } catch (CrmIntegrationException ex) {
                System.err.println("CRM Integration Failed: " + ex.getMessage());
                throw new AmqpRejectAndDontRequeueException("Routing message to DLQ due to CRM failure", ex);
            }
        }

        System.out.println("=================================================");
    }
}
