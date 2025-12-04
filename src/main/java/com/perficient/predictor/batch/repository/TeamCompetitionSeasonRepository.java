package com.perficient.predictor.batch.repository;

import com.perficient.predictor.batch.entity.TeamCompetitionSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamCompetitionSeasonRepository extends JpaRepository<TeamCompetitionSeason, Long> {
    /**
     * Finds an entry based on the unique key constraint:
     * Team Name, Competition Name, and Season Start Year.
     */
    Optional<TeamCompetitionSeason> findByCompetition_NameAndSeasonStartYear(
            String competitionName,
            Integer seasonStartYear
    );
}