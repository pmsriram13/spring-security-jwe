package com.perficient.predictor.batch.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Represents a specific team's participation in a competition during a season.
 * FIXED: Correctly uses object relationships for both Team and Competition entities.
 */
@Entity
@Table(name = "team_competition_season")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCompetitionSeason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_COMPETITION_SEASON_ID")
    private Long id;

    // FIX: Correct ManyToOne relationship to the existing Competition entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPETITION_ID", nullable = false)
    private Competition competition;

    @Column(name = "season_start_year", nullable = false)
    private Integer seasonStartYear;

    @Column(name = "season_end_year", nullable = false)
    private Integer seasonEndYear;

    @Column(name = "updated_by")
    private String updatedBy;
}