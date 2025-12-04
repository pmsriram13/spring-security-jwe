package com.perficient.predictor.batch.dto;

/**
 * Represents the structure of a single row in the Country CSV file as an immutable record.
 * This is the input model used by the Spring Batch ItemReader.
 */
public record CountryCsvInput(
        String countryId,   // e.g., "USA"
        String countryName  // e.g., "United States"
) {
    // Standard record features are automatically included.
}