package com.sprint.training.repository;

import com.sprint.training.model.AccessZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessZoneRepository extends JpaRepository<AccessZone, Long> {
    Optional<AccessZone> findByZoneName(String zoneName);
}
