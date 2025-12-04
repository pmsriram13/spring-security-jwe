package com.perficient.predictor.batch.repository;


import com.perficient.predictor.batch.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    /** Finds a Competition entity by its name. Used for checking uniqueness. */
    Optional<Competition> findByName(String name);
}