package com.perficient.predictor.batch.controller;

import com.perficient.predictor.batch.exception.JobExecutionException;
import com.perficient.predictor.batch.service.DataVersionService;
import com.perficient.predictor.batch.service.TeamJobExecutionService;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller responsible for initiating the Team Load batch process.
 * This is the final, refactored version. It is a "thin controller," meaning it
 * handles HTTP requests and delegates all business logic (version management,
 * job launching) to dedicated service layers, strictly adhering to the Single
 * Responsibility Principle (SRP).
 */
@RestController
@RequestMapping("/api/batch")
public class TeamJobController {

    // Hardcoded location for external files (input and error output)
    private static final String BASE_DIR = "/opt/predictor/data/";
    private static final String ENTITY_NAME = "TEAM"; // The entity name for the version counter

    private final DataVersionService versionService;
    private final TeamJobExecutionService jobExecutionService;

    @Autowired
    public TeamJobController(
            DataVersionService versionService,
            TeamJobExecutionService jobExecutionService) {
        this.versionService = versionService;
        this.jobExecutionService = jobExecutionService;
    }

    /**
     * Triggers the team loading batch job via an HTTP POST request.
     * * @param fileName The name of the input CSV file (e.g., 'team_data.csv').
     * @param countryCode The country code for filtering/processing.
     * @return ResponseEntity with the job execution details or an error message.
     */
    @PostMapping("/teams/load")
    public ResponseEntity<String> loadTeams(
            @RequestParam("fileName") String fileName,
            @RequestParam("countryCode") String countryCode) {

        // 1. Prepare Inputs: Construct the absolute file paths based on the base directory
        String inputFile = BASE_DIR + fileName;
        String errorFile = BASE_DIR + fileName.replace(".csv", ".error.csv");

        // 2. Delegate: Handle data version increment (Database/DAO concern)
        long newVersion;
        try {
            newVersion = versionService.getAndIncrementVersion(ENTITY_NAME);
        } catch (Exception e) {
            System.err.println("Version Service Error: Failed to read/increment data version: " + e.getMessage());
            // Return HTTP 500 if the core dependency (version tracking) fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to prepare data version counter.");
        }

        // 3. Delegate: Execute job (Batch Orchestration concern)
        try {
            JobExecution jobExecution = jobExecutionService.executeTeamLoadJob(
                    countryCode,
                    newVersion,
                    inputFile,
                    errorFile
            );

            // 4. Respond: Return HTTP 200 with job details
            return ResponseEntity.ok(
                    "Team Load Job started successfully. " +
                            "Input File: " + inputFile +
                            ", Error File: " + errorFile +
                            ", New Version Count: " + newVersion +
                            ". Execution ID: " + jobExecution.getId() +
                            ". Status: " + jobExecution.getStatus()
            );

        } catch (JobExecutionException e) {
            // 5. Respond: Handle job startup failures
            System.err.println("Job Execution Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start job: " + e.getMessage());
        }
    }
}