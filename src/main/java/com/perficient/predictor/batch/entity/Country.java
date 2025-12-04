package com.perficient.predictor.batch.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing the 'COUNTRY' database table.
 * Used by the repository to look up the internal ID based on the code.
 */
@Entity
@Table(name = "COUNTRY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    // Assuming COUNTRY_ID is the primary key (BIGINT)
    @Id
    @Column(name = "COUNTRY_ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", nullable = false)
    private String updatedBy;

    // Assuming COUNTRY_CODE is the 3-letter code (VARCHAR)
    @Column(name = "CODE", length = 3, unique = true, nullable = false)
    private String countryCode;

    // Other fields can be added here if needed, but only ID and Code are required for the lookup.
}