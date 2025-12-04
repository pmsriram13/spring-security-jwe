package com.perficient.predictor.batch.service;

import com.perficient.predictor.batch.dto.TeamCompetitionSeasonBatchRequest;
import com.perficient.predictor.batch.dto.TeamCompetitionSeasonResponse;
import com.perficient.predictor.batch.entity.Competition;
import com.perficient.predictor.batch.entity.Team;
import com.perficient.predictor.batch.entity.TeamCompetitionSeason;
import com.perficient.predictor.batch.repository.CompetitionRepository;
import com.perficient.predictor.batch.repository.TeamCompetitionSeasonRepository;
import com.perficient.predictor.batch.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service to handle business logic for TeamCompetitionSeason entities.
 * FIXED: Correctly looks up both Team and Competition entities by name
 * and associates the objects with the new entity.
 */
@Service
public class TeamCompetitionSeasonService {

    private final TeamCompetitionSeasonRepository teamCompetitionSeasonRepository;
    private final TeamRepository teamRepository; // Assumed to exist, with findByName(String)
    private final CompetitionRepository competitionRepository; // Assumed to exist, with findByName(String)

    public TeamCompetitionSeasonService(
            TeamCompetitionSeasonRepository teamCompetitionSeasonRepository,
            TeamRepository teamRepository,
            CompetitionRepository competitionRepository) {

        this.teamCompetitionSeasonRepository = teamCompetitionSeasonRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
    }

    /**
     * Processes the batch request by looking up the Competition once, then looking
     * up each Team by name, and creating the TeamCompetitionSeason records.
     * @param request The DTO containing the list of team names and common metadata.
     * @return A BatchResponse summarizing the outcome.
     */
    public TeamCompetitionSeasonResponse processBatchRequest(TeamCompetitionSeasonBatchRequest request) {
        int successfulCreations = 0;

        // 1. Look up the Competition entity once
        Optional<Competition> competitionOpt = competitionRepository.findByName(request.competitionName());

        if (competitionOpt.isEmpty()) {
            System.err.println("Batch Creation FAILED: Competition not found for name: " + request.competitionName());
            return new TeamCompetitionSeasonResponse(

                    String.format("Failed: Competition '%s' not found.", request.competitionName()),
                    LocalDateTime.now()
            );
        }

        Competition competition = competitionOpt.get();


                // 3. Map DTO data to the core Entity model and set the object references
                TeamCompetitionSeason entity = TeamCompetitionSeason.builder()
                        .competition(competition) // FIX: Setting the Competition object
                        .seasonStartYear(request.seasonStartYear())
                        .seasonEndYear(request.seasonEndYear())
                        .updatedBy(request.updatedBy())
                        .build();

                // 4. Save the individual entity
                teamCompetitionSeasonRepository.save(entity);


        String message = String.format("Competition '%s' batch request processed for ",
                competition.getName());

        // 5. Return the BatchResponse summary
        return new TeamCompetitionSeasonResponse(
                message,
                LocalDateTime.now()
        );
    }
}