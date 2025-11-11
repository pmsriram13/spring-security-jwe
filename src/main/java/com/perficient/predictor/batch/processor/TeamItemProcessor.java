package com.perficient.predictor.batch.processor;

import com.perficient.predictor.batch.dto.TeamInput;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Validates and prepares the TeamInput DTO before writing to the TEAM table.
 */
@Component
public class TeamItemProcessor implements ItemProcessor<TeamInput, TeamInput> {

    private static final String SYSTEM_USER = "TEAM_LOAD_JOB";

    @Override
    public TeamInput process(TeamInput teamInput) throws Exception {
        // Simple validation to ensure core fields are present
        if (!StringUtils.hasText(teamInput.name()) ||
                !StringUtils.hasText(teamInput.teamType()) ||
                !StringUtils.hasText(teamInput.countryCode())) {

            // Skip the record if critical fields are missing
            System.err.println("Skipping team record due to missing core data: " + teamInput.name());
            return null;
        }

        // Set the audit field "updatedBy" since the DTO will be written directly
        teamInput = new TeamInput(
                teamInput.name(),
                teamInput.teamType(),
                teamInput.stadiumName(),
                teamInput.establishedYear(),
                teamInput.nickname(),
                teamInput.stadiumCapacity(),
                teamInput.countryCode(),
                SYSTEM_USER // Set updatedBy to indicate the batch job
        );

        return teamInput;
    }
}