package com.perficient.predictor.batch.listener;

import com.perficient.predictor.batch.dto.TeamCsvInput;
import com.perficient.predictor.batch.dto.TeamDBOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Listener to log details of skipped records during the TEAM data load process.
 */
@Component
public class TeamLoadSkipListener implements SkipListener<TeamCsvInput, TeamDBOutput> {

    private static final Logger log = LoggerFactory.getLogger(TeamLoadSkipListener.class);

    // --- Skips during the ItemProcessor step ---
    @Override
    public void onSkipInProcess(@NonNull TeamCsvInput item, @NonNull Throwable t) {
        // This handles the ValidationException thrown in TeamItemProcessor
        log.warn("--- ITEM SKIPPED IN PROCESSOR ---");
        log.warn("Record Data: Name='{}', Type='{}', Country='{}'",
                item.name(),
                item.teamType(),
                item.countryCode());
        log.warn("Reason for Skip (Validation/Processing): {}", t.getMessage());
    }

    // --- Skips during the ItemWriter step ---
    @Override
    public void onSkipInWrite(@NonNull TeamDBOutput item, @NonNull Throwable t) {
        // This usually handles exceptions like SQL integrity violations
        log.error("--- ITEM SKIPPED IN WRITER ---");
        log.error("Failed TeamDBOutput: Name='{}'", item.name());
        log.error("Reason for Skip (Database Error): {}", t.getMessage());
    }

    // --- Skips during the ItemReader step ---
    @Override
    public void onSkipInRead(@NonNull Throwable t) {
        // This typically handles parsing errors in the CsvItemReader itself
        log.error("--- ITEM SKIPPED IN READER ---");
        log.error("Reason for Skip (File Read Error): {}", t.getMessage());
    }
}