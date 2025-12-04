package com.perficient.predictor.batch.config;

import com.perficient.predictor.batch.dto.TeamCsvInput;
import com.perficient.predictor.batch.dto.TeamDBOutput;
import com.perficient.predictor.batch.exception.ValidationException;
import com.perficient.predictor.batch.listener.TeamLoadSkipListener;
import com.perficient.predictor.batch.processor.TeamItemProcessor;
import com.perficient.predictor.batch.service.lookup.CountryIdLookupService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;

/**
 * Configures the Spring Batch job for loading Team master data.
 * The writer now uses the H2-specific MERGE INTO ... KEY statement to perform
 * a reliable UPSERT (Update or Insert) for idempotent and refreshing loads.
 */
@Configuration
@EnableBatchProcessing
public class TeamLoadConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // Standard constructor injection for required framework beans
    public TeamLoadConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    // --- Reader Configuration (Unchanged) ---
// Inside your Batch Configuration class

    /**
     * Creates the ItemReader, scoped to the Step.
     * It uses the @Value annotation combined with SpEL to read the 'inputFilePath'
     * Job Parameter passed by the controller at runtime.
     */
    @Bean
    @StepScope // MANDATORY: This scopes the bean to the step execution lifecycle
    public FlatFileItemReader<TeamCsvInput> teamReader(
            // Inject the Job Parameter named 'inputFilePath'
            @Value("#{jobParameters['inputFilePath']}") String inputFilePath) {
// Add the "file:" prefix if it's missing, to force filesystem resolution
        String safePath = inputFilePath.startsWith("file:") ? inputFilePath : "file:" + inputFilePath;
        // You now need to convert the String path into a Spring Resource manually
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(safePath);

        // Names must match the TeamCsvInput record fields and the CSV column order
        return new FlatFileItemReaderBuilder<TeamCsvInput>()
                .name("teamItemReader")
                .resource(resource) // Use the dynamic resource
                .delimited()
                .names("name", "teamType", "stadiumName", "establishedYear", "nickname", "stadiumCapacity", "countryCode")
                .targetType(TeamCsvInput.class)
                .linesToSkip(1) // Skip header row
                .build();
    }

    // --- Processor Configuration (Unchanged) ---
    @Bean
    public TeamItemProcessor teamProcessor(CountryIdLookupService countryIdLookupService) {
        // Corrected: Pass the injected service into the constructor
        return new TeamItemProcessor(countryIdLookupService);
    }

    // --- Writer Configuration Class (NEW H2 MERGE WITH KEY) ---
    @Configuration
    public class TeamItemWriterConfig {

        /**
         * Configures the writer to use the reliable H2 MERGE INTO ... KEY statement.
         * This performs a true UPSERT: updating existing records and inserting new ones.
         */
        @Bean
        public JdbcBatchItemWriter<TeamDBOutput> teamWriter(DataSource dataSource) {

            // H2-specific MERGE syntax using the KEY clause.
            // If a row with the matching NAME exists, it is UPDATED; otherwise, it is INSERTED.
            final String upsertSql = """
            MERGE INTO TEAM 
            (NAME, TEAM_TYPE, STADIUM_NAME, ESTABLISHED_YEAR, NICKNAME, STADIUM_CAPACITY, COUNTRY_ID, UPDATED_BY)
            KEY (NAME) 
            VALUES 
            (:name, :teamType, :stadiumName, :establishedYear, :nickname, :stadiumCapacity, :countryCode, :updatedBy)
            """;

            return new JdbcBatchItemWriterBuilder<TeamDBOutput>()
                    .dataSource(dataSource)
                    .sql(upsertSql)
                    // Maps TeamDBOutput properties (name, teamType, etc.) to the SQL parameters
                    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                    .build();
        }
    }

    // --- Step Configuration (Unchanged) ---
    @Bean
    public Step teamLoadStep(FlatFileItemReader<TeamCsvInput> teamReader,
                             TeamItemProcessor teamProcessor,
                             JdbcBatchItemWriter<TeamDBOutput> teamWriter,
                             TeamLoadSkipListener teamLoadSkipListener) {

        return new StepBuilder("teamLoadStep", jobRepository)
                .<TeamCsvInput, TeamDBOutput>chunk(10, transactionManager)
                .reader(teamReader)
                .processor(teamProcessor)
                .writer(teamWriter)
                // --- FAULT TOLERANCE CONFIGURATION ---
                .faultTolerant()
                .skipLimit(100)

                // Keep fault tolerance for validation and unexpected runtime errors.
                .skip(ValidationException.class)
                .skip(DataAccessException.class)
                .skip(RuntimeException.class)
                .listener(teamLoadSkipListener)

                .build();
    }

    // --- Job Configuration (Unchanged) ---
    @Bean
    public Job teamLoadJob(Step teamLoadStep) {
        return new JobBuilder("teamLoadJob", jobRepository)
                .start(teamLoadStep)
                .build();
    }
}