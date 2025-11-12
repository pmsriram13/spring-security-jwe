package com.perficient.predictor.batch.dto;

/**
 * Data Transfer Object for reading team data from a flat file (CSV)
 * into the initial Team Load Batch Job.
 * * Implemented as a Java Record for conciseness, automatically providing
 * a canonical constructor, accessor methods, equals(), hashCode(), and toString().
 */
public record TeamDBOutput(
        String name,
        String teamType, // e.g., 'CLUB', 'NATIONAL'
        String stadiumName,
        Integer establishedYear,
        String nickname,
        Integer stadiumCapacity,
        String countryCode, // e.g., 'ENG'
        String updatedBy // For audit trail/writer compatibility
) {
    // Note: No body needed. The compiler automatically generates the constructor and accessors.
}