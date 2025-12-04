package com.perficient.predictor.batch.controller;

import com.perficient.predictor.batch.exception.JobExecutionException;
import com.perficient.predictor.batch.service.DataVersionService;
// Injecting the service for the new entity batch
import com.perficient.predictor.batch.service.CountryJobExecutionService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller responsible for initiating the Country Job Load batch process.
 * This controller mirrors the structure of TeamJobController.
 * It handles HTTP requests and delegates all business logic (version management,
 * job launching) to dedicated service layers.
 */
@RestController
@RequestMapping("/api/batch")
public class CountryJobController {

    // Hardcoded location for external files (input and error output)
    private static final String BASE_DIR = "/opt/predictor/data/";
    // The entity name for the version counter (changed from TEAM to COUNTRY_JOB)
    private static final String ENTITY_NAME = "COUNTRY_JOB";

    private final DataVersionService versionService;
    private final CountryJobExecutionService jobExecutionService; // Injected Country Job Service

    @Autowired
    public CountryJobController(
            DataVersionService versionService,
            CountryJobExecutionService jobExecutionService) {
        this.versionService = versionService;
        this.jobExecutionService = jobExecutionService;
    }

    /**
     * Triggers the Country Job loading batch job via an HTTP POST request.
     *
     * @param fileName The name of the input CSV file (e.g., 'country_job_data.csv').
     * NOTE: The countryCode parameter is intentionally omitted as requested.
     * @return ResponseEntity with the job execution details or an error message.
     */
    @PostMapping("/countryjobs/load") // Updated mapping
    public ResponseEntity<String> loadCountryJobs(
            @RequestParam("fileName") String fileName) { // Removed countryCode parameter

        // 1. Prepare Inputs: Construct the absolute file paths based on the base directory
        String inputFile = BASE_DIR + fileName;
        String errorFile = BASE_DIR + fileName.replace(".csv", ".error.csv");

        // 2. Delegate: Handle data version increment (Database/DAO concern)
        long newVersion;
        try {
            newVersion = versionService.getAndIncrementVersion(ENTITY_NAME);
        } catch (Exception e) {
            System.err.println("Version Service Error: Failed to read/increment data version for " + ENTITY_NAME + ": " + e.getMessage());
            // Return HTTP 500 if the core dependency (version tracking) fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to prepare data version counter.");
        }

        // 3. Delegate: Execute job (Batch Orchestration concern)
        try {
            // Updated service method name and removed the countryCode argument
            JobExecution jobExecution = jobExecutionService.executeCountryLoadJob(
                    newVersion,
                    inputFile,
                    errorFile
            );

            // 4. Respond: Return HTTP 200 with job details
            return ResponseEntity.ok(
                    "Country Job Load Job started successfully. " + // Updated entity name in response
                            "Input File: " + inputFile +
                            ", Error File: " + errorFile +
                            ", New Version Count: " + newVersion +
                            ". Execution ID: " + jobExecution.getId() +
                            ". Status: " + jobExecution.getStatus()
            );

        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}