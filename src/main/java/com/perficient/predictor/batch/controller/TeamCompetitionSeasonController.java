package com.perficient.predictor.batch.controller;

// Import the concrete class
import com.perficient.predictor.batch.dto.TeamCompetitionSeasonBatchRequest;
import com.perficient.predictor.batch.dto.TeamCompetitionSeasonResponse;
import com.perficient.predictor.batch.service.TeamCompetitionSeasonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/seasons")
public class TeamCompetitionSeasonController {

    // Now uses the concrete service class directly
    private final TeamCompetitionSeasonService service;

    public TeamCompetitionSeasonController(TeamCompetitionSeasonService service) {
        this.service = service;
    }

    /**
     * Handles a batch request to create multiple team-competition-season records.
     * Maps the BatchRequest DTO to the service layer for processing.
     */
    @PostMapping
    public ResponseEntity<TeamCompetitionSeasonResponse> createTeamSeasonBatch(
            @Valid @RequestBody TeamCompetitionSeasonBatchRequest request) {

        TeamCompetitionSeasonResponse response = service.processBatchRequest(request);

        // Return 201 Created for a successful operation
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}