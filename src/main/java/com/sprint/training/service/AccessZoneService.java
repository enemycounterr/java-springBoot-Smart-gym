package com.sprint.training.service;

import com.sprint.training.dto.zone.AccessZoneCreateRequest;
import com.sprint.training.dto.zone.AccessZoneResponse;
import com.sprint.training.mapper.AccessZoneMapper;
import com.sprint.training.model.AccessZone;
import com.sprint.training.repository.AccessZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AccessZoneService {
    private final AccessZoneRepository zoneRepository;
    private final AccessZoneMapper mapper;

    public AccessZoneService(AccessZoneRepository repository, AccessZoneMapper mapper) {
        this.zoneRepository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public AccessZoneResponse createZone(AccessZoneCreateRequest request){
        if (zoneRepository.findByZoneName(request.zoneName().toUpperCase()).isPresent()){
            throw new IllegalArgumentException("Zone with name " + request.zoneName() + " already exists");
        }

        AccessZone zone = mapper.toEntity(request);
        return mapper.toDto(zoneRepository.save(zone));
    }

    @Transactional(readOnly = true)
    public List<AccessZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
