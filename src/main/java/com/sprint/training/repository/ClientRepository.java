package com.sprint.training.repository;


import com.sprint.training.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"accessCard"})
    @Query("SELECT c FROM Client c")
    List<Client> findAllWithAccessCard();

    @EntityGraph(attributePaths = {"accessCard"})
    @Query("""
            SELECT c FROM Client 
            c JOIN c.accessZones z 
            WHERE z.id = :zoneId
    """
    )
    List<Client> findAllByAccessZoneId(@Param("zoneId") Long zoneId);

    @EntityGraph(attributePaths = {"accessZones"})
    @Query("SELECT c FROM Client c WHERE c.id = :id")
    Optional<Client> findByIdWithZones(Long id);
}
