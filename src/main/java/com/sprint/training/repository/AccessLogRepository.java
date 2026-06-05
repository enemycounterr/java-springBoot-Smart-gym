package com.sprint.training.repository;

import com.sprint.training.model.AccessLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findAllByClientId(Long clientId);
    boolean existsByAccessZoneId(Long zoneId);

    @EntityGraph(attributePaths = {"client", "accessZone"})
    @Query("SELECT l FROM AccessLog l")
    List<AccessLog> findAllWithClientAndZone();
}
