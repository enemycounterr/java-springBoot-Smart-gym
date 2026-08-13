package com.sprint.training.integration;


import com.sprint.training.exceptions.CrmIntegrationException;
import com.sprint.training.metrics.service.MetricsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CrmClient {

    private final RestClient restClient;
    private final MetricsService metricsService;

    public CrmClient(@Value("${integration.crm.base-url}") String baseUrl, MetricsService metricsService) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.metricsService = metricsService;
    }

    public void sendLoyaltyPoints(Long clientId, String clientName) {
        long startTime = System.currentTimeMillis();

        try {
            this.restClient.post()
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

            this.metricsService.incrementCrmSuccess();

        } catch (RestClientException e) {
            String errorType = (e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout"))
                    ? "TIMEOUT"
                    : "REST_CLIENT_ERROR";
            this.metricsService.incrementCrmError(errorType);

            throw new CrmIntegrationException("Failed to send data to CRM: " + e.getMessage());
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            this.metricsService.recordCrmCallDuration(duration);
        }
    }
}
