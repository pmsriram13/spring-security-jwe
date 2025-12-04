package com.perficient.predictor.batch.repository;

import com.perficient.predictor.batch.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for the Country entity.
 * It provides standard CRUD and querying capabilities for Country records,
 * using Long (the ID type) as the primary key.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Finds a Country entity based on its unique 3-letter country code.
     * Spring Data JPA automatically implements this method based on the method name.
     *
     * @param countryCode The 3-letter country code (e.g., "USA", "CAN").
     * @return The Country entity if found, or null otherwise.
     */
    Optional<Country> findByCountryCode(String countryCode); // Changed return type to Optional

    /** Finds a Country entity by its name. */
    Optional<Country> findByName(String name);
}