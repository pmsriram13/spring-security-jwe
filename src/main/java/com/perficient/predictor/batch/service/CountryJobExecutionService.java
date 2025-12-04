package com.perficient.predictor.batch.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
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
 * Service class responsible for initiating the Country Load Batch Job.
 * It injects the JobLauncher and the specific Job bean to execute the batch process.
 */
@Service
public class CountryJobExecutionService {

    private final JobLauncher jobLauncher;
    // The specific Job bean that handles reading, processing, and writing country data.
    private final Job countryLoadJob;

    /**
     * Constructor for injecting the necessary Spring Batch components.
     * @param jobLauncher The utility for launching jobs asynchronously or synchronously.
     * @param countryLoadJob The specific Job bean configured to load country data.
     */
    @Autowired
    public CountryJobExecutionService(
            JobLauncher jobLauncher,
            @Qualifier("countryLoadJob") Job countryLoadJob) {
        this.jobLauncher = jobLauncher;
        this.countryLoadJob = countryLoadJob;
    }

    /**
     * Executes the Country Load Batch Job.

     * @return The JobExecution result.
     * @throws JobExecutionAlreadyRunningException if the job is already running with the same parameters.
     * @throws JobRestartException if the job cannot be restarted.
     * @throws JobInstanceAlreadyCompleteException if the job has already completed successfully.
     * @throws JobParametersInvalidException if the parameters are invalid.
     */
    public JobExecution executeCountryLoadJob(long dataVersion,
                                              String inputFilePath,
                                              String errorFilePath) throws
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException,
            JobParametersInvalidException {

        // 1. Create unique JobParameters for the execution to ensure restartability.
        // We use a timestamp and the file name to ensure uniqueness.
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("jobId", String.valueOf(System.currentTimeMillis()))
                .addLocalDateTime("executionTime", LocalDateTime.now())
                .addLong("dataVersion", dataVersion).
                addString("inputFilePath", inputFilePath).
                addString("errorFilePath", errorFilePath)
                .toJobParameters();

        System.out.println("Launching Country Load Job with parameters: " + jobParameters);

        // 2. Launch the job.
        return jobLauncher.run(countryLoadJob, jobParameters);
    }
}