package com.perficient.predictor.batch.entity;

import com.perficient.predictor.batch.entity.Country;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entity mapping to the TEAM table based on the provided DDL.
 */
@Entity
@Table(name = "TEAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_ID")
    private Long teamId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "TEAM_TYPE", nullable = false)
    private String teamType; // 'CLUB' or 'NATIONAL'

    // Foreign Key mapping to the existing Country entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID", nullable = false)
    private Country country;

    // Club Specific Details
    @Column(name = "STADIUM_NAME")
    private String stadiumName;

    @Column(name = "ESTABLISHED_YEAR")
    private Integer establishedYear;

    @Column(name = "NICKNAME")
    private String nickname;

    @Column(name = "STADIUM_CAPACITY")
    private Integer stadiumCapacity;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", nullable = false)
    private String updatedBy;
}