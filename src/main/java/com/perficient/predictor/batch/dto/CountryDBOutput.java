package com.perficient.predictor.batch.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for writing validated and processed Country records to the database.
 */

public record CountryDBOutput(
    String code,
    String name,
    String updatedBy
    )// For audit trail/writer compatibility
 {
    // Note: No body needed. The compiler automatically generates the constructor and accessors.
}
