package com.perficient.predictor.batch.dto;


/**
 * Data Transfer Object for incoming POST requests to create a new season entry.
 * Uses Java Record for immutability and conciseness.
 */
import java.util.List;

/**
 * Data Transfer Object for incoming POST requests for a batch operation.
 * This structure allows saving multiple teams for the same competition and season
 * in a single, efficient request, minimizing duplication.
 */
public record TeamCompetitionSeasonBatchRequest(

        String competitionName,
        Integer seasonStartYear,
        Integer seasonEndYear,
        String updatedBy // Audit field
) {}