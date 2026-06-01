package com.sprint.training.repository;

import com.sprint.training.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findAllByClientId(Long clientId);
    boolean existsByAccessZoneId(Long zoneId);
}
