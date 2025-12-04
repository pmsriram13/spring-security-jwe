package com.perficient.predictor.batch.service;

import com.perficient.predictor.batch.dto.CompetitionRequest;
import com.perficient.predictor.batch.entity.Competition;
import com.perficient.predictor.batch.entity.Country;
import com.perficient.predictor.batch.repository.CompetitionRepository;
import com.perficient.predictor.batch.repository.CountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CountryRepository countryRepository;

    public CompetitionService(CompetitionRepository competitionRepository, CountryRepository countryRepository) {
        this.competitionRepository = competitionRepository;
        this.countryRepository = countryRepository;
    }

    /**
     * Creates a new Competition by looking up the Country entity based on the Country Name.
     *
     * @param request The DTO containing the competition details.
     * @return The newly created Competition entity.
     * @throws IllegalStateException if the Competition name already exists.
     * @throws IllegalArgumentException if the Country is not found.
     */
    @Transactional
    public Competition createCompetition(CompetitionRequest request) {
        // 1. Check for existing competition name (based on UNIQUE constraint)
        if (competitionRepository.findByName(request.name()).isPresent()) {
            throw new IllegalStateException("Competition with name '" + request.name() + "' already exists.");
        }

        // 2. Look up Country entity by name. This is crucial for satisfying the foreign key constraint.
        Country country = countryRepository.findByName(request.countryName())
                .orElseThrow(() -> new IllegalArgumentException("Country '" + request.countryName() + "' not found. Competition must be associated with an existing country."));

        // 3. Create the Competition entity
        Competition newCompetition = new Competition();
        newCompetition.setName(request.name());
        newCompetition.setCountry(country); // JPA handles setting the COUNTRY_ID field automatically

        // Set audit fields
        LocalDateTime now = LocalDateTime.now();
        String updatedBy = "COMPETITION_API_USER"; // Placeholder for logged-in user

        newCompetition.setCreatedAt(now);
        newCompetition.setUpdatedAt(now);
        newCompetition.setUpdatedBy(updatedBy);

        return competitionRepository.save(newCompetition);
    }
}