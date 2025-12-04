package com.perficient.predictor.batch.dto;

import com.perficient.predictor.batch.entity.TeamCompetitionSeason;

/**
 * Data Transfer Object for outgoing responses.
 * Uses Java Record for immutability and conciseness.
 */
import java.time.LocalDateTime;

/**
 * Response DTO for any batch operation indicating the success status.
 */
public record TeamCompetitionSeasonResponse(

        String message,
        LocalDateTime timestamp
) {}