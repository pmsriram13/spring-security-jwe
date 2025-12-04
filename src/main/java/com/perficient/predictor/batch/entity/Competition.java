package com.perficient.predictor.batch.entity;

import com.perficient.predictor.batch.entity.Country;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "COMPETITION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPETITION_ID")
    private Long competitionId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    // We use @ManyToOne mapping to the Country entity via the COUNTRY_ID column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID", nullable = false, columnDefinition = "INT") // Use columnDefinition=INT to match schema
    private Country country;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", nullable = false)
    private String updatedBy;
}