package com.sprint.training.repository;


import com.sprint.training.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"accessCard"})
    @Query("SELECT c FROM Client c")
    List<Client> findAllWithAccessCard();
}
