package com.sprint.training.integration;


import com.sprint.training.exceptions.CrmIntegrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CrmClient {

    private final RestClient restClient;

    public CrmClient(@Value("${integration.crm.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void sendLoyaltyPoints(Long clientId, String clientName) {
        try {
            restClient.post()
                    .uri("/post")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "clientId": %d,
                                "name": "%s",
                                "points": 10
                            }
                            """.formatted(clientId, clientName))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException e) {
            throw new CrmIntegrationException("Failed to send data to CRM: " + e.getMessage());
        }
    }
}
