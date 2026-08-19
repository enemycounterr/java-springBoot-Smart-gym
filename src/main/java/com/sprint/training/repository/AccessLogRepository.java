package com.sprint.training.repository;

import com.sprint.training.model.AccessDirection;
import com.sprint.training.model.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    List<AccessLog> findAllByClientId(Long clientId);

    boolean existsByAccessZoneId(Long zoneId);

    @EntityGraph(attributePaths = {"client", "accessZone"})
    @Query("SELECT l FROM AccessLog l")
    Page<AccessLog> findAllWithClientAndZone(Pageable pageable);

    long countByClientIdAndDirection(Long clientId, AccessDirection direction);

    @Query(
            "SELECT l FROM AccessLog l " +
                    "JOIN FETCH l.client " +
                    "WHERE l.timeStamp = (SELECT MAX(sub.timeStamp) FROM AccessLog sub WHERE sub.client = l.client) " +
                    "AND l.direction = :direction"
    )
    List<AccessLog> findCurrentClientsInside(@Param("direction") AccessDirection direction);

    Optional<AccessLog> findFirstByClientIdOrderByTimeStampDesc(Long clientId);
}
