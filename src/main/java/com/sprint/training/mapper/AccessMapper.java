package com.sprint.training.mapper;


import com.sprint.training.dto.access.AccessCheckRequest;
import com.sprint.training.dto.access.AccessLogResponse;
import com.sprint.training.model.AccessLog;
import com.sprint.training.model.AccessZone;
import com.sprint.training.model.Client;
import org.springframework.stereotype.Component;

@Component
public class AccessMapper {

    public AccessLogResponse toDto(AccessLog log) {
        if (log == null) return null;

        return new AccessLogResponse(
                log.getId(),
                log.getClient().getId(),
                log.getClient().getName(),
                log.getAccessZone().getZoneName(),
                log.getDirection(),
                log.getTimeStamp()
        );
    }

    public AccessLog toEntity(AccessCheckRequest request, Client client, AccessZone accessZone) {
        if (request == null || client == null || accessZone == null) return null;

        return new AccessLog(request.direction(), client, accessZone);


    }
}
