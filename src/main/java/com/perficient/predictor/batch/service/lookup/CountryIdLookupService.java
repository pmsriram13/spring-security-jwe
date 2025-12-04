package com.perficient.predictor.batch.service.lookup;

import com.perficient.predictor.batch.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for resolving the external 3-letter country code
 * into the internal BIGINT Country ID using Spring Data JPA.
 */
@Service
public class CountryIdLookupService {

    private final CountryRepository countryRepository;

    /**
     * Injects the Spring Data JPA repository.
     */
    public CountryIdLookupService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Finds the internal Country ID based on the external 3-letter country code.
     * The lookup is performed in a read-only transaction.
     * @param countryCode The 3-letter country code (e.g., "ENG").
     * @return The corresponding Country ID (Long), or null if the code is not found in the database.
     */
    @Transactional(readOnly = true)
    public Long findIdByCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return null;
        }

        final String normalizedCode = countryCode.toUpperCase();

        return countryRepository.findByCountryCode(normalizedCode)
                .map(country -> {
                    System.out.println("JPA Lookup: Country code '" + normalizedCode + "' resolved to ID: " + country.getId());
                    return country.getId();
                })
                .orElseGet(() -> {
                    System.err.println("JPA Lookup: Country code '" + normalizedCode + "' not found in the COUNTRY table. Returning null.");
                    return null;
                });
    }
}