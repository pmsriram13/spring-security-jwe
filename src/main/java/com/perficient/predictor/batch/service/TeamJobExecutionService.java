package com.perficient.predictor.batch.service;

import com.perficient.predictor.batch.exception.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service responsible for launching the Team Load Spring Batch Job.
 * This replaces the direct JobLauncher usage in the controller.
 */
@Service
public class TeamJobExecutionService {

    private static final String JOB_NAME = "teamLoadJob";

    private final JobLauncher jobLauncher;
    private final Job teamLoadJob;

    @Autowired
    public TeamJobExecutionService(
            JobLauncher jobLauncher,
            @Qualifier(JOB_NAME) Job teamLoadJob) {
        this.jobLauncher = jobLauncher;
        this.teamLoadJob = teamLoadJob;
    }

    /**
     * Executes the Team Load Batch Job with the given parameters.
     * * @param countryCode The country code to process.
     * @param dataVersion The unique incremental version count for this run.
     * @param inputFilePath The absolute path to the input CSV file.
     * @param errorFilePath The absolute path to the error CSV file.
     * @return The JobExecution object containing the status and ID of the run.
     * @throws JobExecutionException If the job fails to start.
     */
    public JobExecution executeTeamLoadJob(
            String countryCode,
            long dataVersion,
            String inputFilePath,
            String errorFilePath) throws JobExecutionException {

        // Build unique job parameters
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("countryCode", countryCode);
        builder.addLong("dataVersion", dataVersion);
        builder.addString("inputFilePath", inputFilePath);
        builder.addString("errorFilePath", errorFilePath);
        // Use a unique time parameter to ensure the job instance is always new
        builder.addLocalDateTime("time", LocalDateTime.now(), true);

        try {
            System.out.println("Launching Team Load Job. Version: " + dataVersion +
                    ", File: " + inputFilePath);

            return jobLauncher.run(teamLoadJob, builder.toJobParameters());

        } catch (JobExecutionAlreadyRunningException e) {
            throw new JobExecutionException("A job instance with the same parameters is currently running.", e);
        } catch (JobRestartException | JobParametersInvalidException | JobInstanceAlreadyCompleteException e) {
            throw new JobExecutionException("Failed to start job due to invalid parameters or completion status.", e);
        }
    }
}