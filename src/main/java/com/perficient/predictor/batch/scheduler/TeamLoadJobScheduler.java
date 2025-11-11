package com.perficient.predictor.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled component that manages the execution of the teamLoadJob.
 * It ensures the job runs only once for a specific data version by checking
 * the DATA_VERSION table before execution.
 */
@Component
public class TeamLoadJobScheduler {

    private static final String DATA_VERSION = "1.0.0";
    private static final String JOB_NAME = "teamLoadJob";
    private static final String COUNTRY_CODE = "ENG";

    private final JobLauncher jobLauncher;
    private final Job teamLoadJob;
    private final JdbcTemplate jdbcTemplate;

    public TeamLoadJobScheduler(
            JobLauncher jobLauncher,
            @Qualifier(JOB_NAME) Job teamLoadJob,
            JdbcTemplate jdbcTemplate) {
        this.jobLauncher = jobLauncher;
        this.teamLoadJob = teamLoadJob;
        this.jdbcTemplate = jdbcTemplate;
        System.out.println("Srriam: TeamLoadJobScheduler initialized for job: " + JOB_NAME);
    }

    /**
     * Executes the job if the current DATA_VERSION has not been successfully loaded.
     * Runs 60 seconds after application startup and every 60 seconds thereafter (fixed delay)
     * until the job completes successfully.
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void runOneTimeTeamLoadJob() {
        // 1. Check if version is already loaded
        boolean isLoaded = isDataVersionLoaded(DATA_VERSION);

        if (isLoaded) {
            System.out.println("Data version " + DATA_VERSION + " is already loaded. Skipping " + JOB_NAME + ".");
            return;
        }

        // 2. Build unique job parameters (ensures Spring Batch sees it as a new instance)
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("countryCode", COUNTRY_CODE);
        builder.addString("dataVersion", DATA_VERSION);
        builder.addLocalDateTime("time", LocalDateTime.now()); // Unique timestamp for re-runs/retries

        try {
            System.out.println("Starting one-time Team Load Job for version: " + DATA_VERSION);

            // 3. Launch the job
            jobLauncher.run(teamLoadJob, builder.toJobParameters());

            // 4. If job succeeds, record the version in the database
            recordDataVersion(DATA_VERSION);
            System.out.println("Team Load Job completed successfully. Version " + DATA_VERSION + " recorded.");

        } catch (JobExecutionException e) {
            System.err.println("Team Load Job failed for version " + DATA_VERSION + ": " + e.getMessage());
            // Important: If it fails, we do NOT record the version, allowing it to retry later.
        }
    }

    private boolean isDataVersionLoaded(String version) {
        String sql = "SELECT COUNT(*) FROM DATA_VERSION WHERE VERSION = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, version);
        return count != null && count > 0;
    }

    private void recordDataVersion(String version) {
        String sql = "INSERT INTO DATA_VERSION (VERSION, LOAD_DATE) VALUES (?, NOW())";
        jdbcTemplate.update(sql, version);
    }
}