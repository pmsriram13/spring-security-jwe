package com.perficient.predictor.batch.processor;

import com.perficient.predictor.batch.dto.TeamCsvInput;
import com.perficient.predictor.batch.dto.TeamDBOutput;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Validates and prepares the TeamInput DTO before writing to the TEAM table.
 */
@Component
public class TeamItemProcessor implements ItemProcessor<TeamCsvInput, TeamDBOutput> {

    private static final String SYSTEM_USER = "TEAM_LOAD_JOB";

    @Override
    public TeamDBOutput process(TeamCsvInput teamInput) throws Exception {
        // Simple validation to ensure core fields are present
        if (!StringUtils.hasText(teamInput.name()) ||
                !StringUtils.hasText(teamInput.teamType()) ||
                !StringUtils.hasText(teamInput.countryCode())) {

            // Skip the record if critical fields are missing
            System.err.println("Skipping team record due to missing core data: " + teamInput.name());
            return null;
        }
        Integer establishedYear = Integer.parseInt(teamInput.establishedYear());
        Integer stadiumCapacity = Integer.parseInt(teamInput.stadiumCapacity());

        // Set the audit field "updatedBy" since the DTO will be written directly
        return new TeamDBOutput(
                teamInput.name(),
                teamInput.teamType(),
                teamInput.stadiumName(),
                establishedYear,
                teamInput.nickname(),
                 stadiumCapacity,
                teamInput.countryCode(),
                SYSTEM_USER // Set updatedBy to indicate the batch job
        );

    }
}