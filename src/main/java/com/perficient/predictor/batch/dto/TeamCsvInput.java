package com.perficient.predictor.batch.dto;
public record TeamCsvInput(
        String name,
        String teamType,
        String stadiumName,
        String establishedYear, // Use String, let the Processor convert it
        String nickname,
        String stadiumCapacity, // Use String, let the Processor convert it
        String countryCode
) {
    // No 'updatedBy' field here
}
