package com.perficient.predictor.batch.dto;


import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for the client-facing Competition response.
 *
 * This DTO explicitly excludes the detailed 'country' object to simplify
 * the API payload, as requested by the client specification.
 */
public class CompetitionResponseDTO {

    private Long competitionId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;

    // Default constructor for Jackson serialization/deserialization
    public CompetitionResponseDTO() {}

    public CompetitionResponseDTO(Long competitionId, String name, LocalDateTime createdAt, LocalDateTime updatedAt, String updatedBy) {
        this.competitionId = competitionId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    // --- Getters and Setters (omitted for brevity in DTOs often, but included for completeness) ---

    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}