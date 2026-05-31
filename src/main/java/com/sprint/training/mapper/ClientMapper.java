package com.sprint.training.mapper;

import com.sprint.training.dto.client.ClientCreateRequest;
import com.sprint.training.dto.client.ClientResponse;
import com.sprint.training.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toDto(Client client) {
        if (client == null) return null;

        String token = (client.getAccessCard() != null ) ? client.getAccessCard().getRfidToken() : null;
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.isActive(),
                token
        );
    }

    public Client toEntity(ClientCreateRequest request) {
        if (request == null) return null;

        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setActive(true);

        return client;
    }
}
