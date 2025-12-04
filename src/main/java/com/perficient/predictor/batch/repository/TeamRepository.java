package com.perficient.predictor.batch.repository;


import com.perficient.predictor.batch.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    /**
     * Finds a Team by its unique name.
     */
    Optional<Team> findByName(String name);
}
