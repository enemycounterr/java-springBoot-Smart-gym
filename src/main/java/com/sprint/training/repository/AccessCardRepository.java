package com.sprint.training.repository;

import com.sprint.training.model.AccessCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessCardRepository extends JpaRepository<AccessCard, Long> {
    Optional<AccessCard> findByRfidToken(String rfidToken);
}
