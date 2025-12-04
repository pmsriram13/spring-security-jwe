package com.perficient.predictor.batch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new Competition via API.
 * The countryName is used to look up the Country ID internally.
 */
public record CompetitionRequest(
        @NotBlank(message = "Competition name is mandatory.")
        @Size(max = 255, message = "Competition name cannot exceed 255 characters.")
        String name,

        @NotBlank(message = "Country name is required to associate the competition.")
        String countryName
) {}