package com.perficient.predictor.batch.processor;

import com.perficient.predictor.batch.dto.TeamCsvInput;
import com.perficient.predictor.batch.dto.TeamDBOutput;
import com.perficient.predictor.batch.exception.ValidationException;
import com.perficient.predictor.batch.service.lookup.CountryIdLookupService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.lang.NonNull;

/**
 * Validates and prepares the TeamInput DTO before writing to the TEAM table.
 * Returns null to filter (skip) records with missing core fields or invalid data formats.
 */
@Component
public class TeamItemProcessor implements ItemProcessor<TeamCsvInput, TeamDBOutput> {

    private static final String SYSTEM_USER = "TEAM_LOAD_JOB";

    // Inject the service responsible for looking up the country ID
    private final CountryIdLookupService countryIdLookupService;

    public TeamItemProcessor(CountryIdLookupService countryIdLookupService) {
        this.countryIdLookupService = countryIdLookupService;
    }


    @Override
    public TeamDBOutput process(@NonNull TeamCsvInput teamInput) {

        // 1. Validation: Check for null or empty strings on critical fields
        if (!StringUtils.hasText(teamInput.name())) {
            throw new ValidationException("Mandatory field missing: Team Name is null or empty.");
        }
        if (!StringUtils.hasText(teamInput.teamType())) {
            // Throw the custom exception. Since this exception is configured in the Step
            // as skippable, Spring Batch will catch it and skip the record.
            throw new ValidationException("Mandatory field missing: Team Type is null or empty for team [" + teamInput.name() + "].");
        }
        if (!StringUtils.hasText(teamInput.countryCode())) {
            throw new ValidationException("Mandatory field missing: Country Code is null or empty for team [" + teamInput.name() + "].");
        }

        // 2. Conversion: Handle potential NumberFormatExceptions gracefully
        Integer establishedYear;
        Integer stadiumCapacity;

        try {
            establishedYear = Integer.parseInt(teamInput.establishedYear());
        } catch (NumberFormatException e) {
            System.err.println("Skipping record: Invalid 'establishedYear' format for team [" + teamInput.name() + "]. Value was: " + teamInput.establishedYear());
            return null;
        }

        try {
            stadiumCapacity = Integer.parseInt(teamInput.stadiumCapacity());
        } catch (NumberFormatException e) {
            System.err.println("Skipping record: Invalid 'stadiumCapacity' format for team [" + teamInput.name() + "]. Value was: " + teamInput.stadiumCapacity());
            return null;
        }
        // 2. Lookup: Resolve Country Code to Country ID
        Long countryId = countryIdLookupService.findIdByCode(teamInput.countryCode());

        if (countryId == null) {
            // Log and skip the record if the country code could not be mapped to an existing ID
            System.err.println("Skipping record: Failed to lookup Country ID for code '" + teamInput.countryCode() + "' for team [" + teamInput.name() + "].");
            return null;
        }

        // 3. Transformation and Mapping (Only executed if all validations and conversions pass)
        return new TeamDBOutput(
                teamInput.name(),
                teamInput.teamType(),
                teamInput.stadiumName(),
                establishedYear, // Validated and converted
                teamInput.nickname(),
                stadiumCapacity, // Validated and converted
                countryId,
                SYSTEM_USER // Set updatedBy to indicate the batch job
        );
    }
}