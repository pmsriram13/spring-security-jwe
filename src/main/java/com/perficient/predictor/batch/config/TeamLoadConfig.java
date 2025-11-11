package com.perficient.predictor.batch.config;

import com.perficient.predictor.batch.dto.TeamInput;
import com.perficient.predictor.batch.processor.TeamItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Configures the Spring Batch job for loading Team master data using JDBC
 * directly with the TeamInput DTO, as no JPA Entity is being used.
 */
@Configuration
@EnableBatchProcessing
public class TeamLoadConfig {

    // --- Reader Configuration ---
    @Bean
    public FlatFileItemReader<TeamInput> teamReader(@Value("${input.file.teams}") Resource resource) {
        // Names must match the TeamInput record fields and the CSV column order
        return new FlatFileItemReaderBuilder<TeamInput>()
                .name("teamItemReader")
                .resource(resource)
                .delimited()
                // These names MUST exactly match the fields in your TeamInput DTO/record
                .names("name", "teamType", "stadiumName", "establishedYear", "nickname", "stadiumCapacity", "countryCode", "updatedBy")
                .targetType(TeamInput.class)
                .linesToSkip(1) // Skip header row
                .build();
    }

    // --- Writer Configuration ---
    /**
     * Configures the writer to use JDBC for direct database insertion.
     * It uses BeanPropertyItemSqlParameterSourceProvider, relying on the
     * TeamInput DTO's accessors (e.g., teamInput.name()) matching the SQL parameter names.
     */
    @Bean
    public JdbcBatchItemWriter<TeamInput> teamWriter(DataSource dataSource) {
        // The SQL parameter names (:name, :teamType, etc.) must match the TeamInput field names.
        final String sql = "INSERT INTO TEAM (NAME, TEAM_TYPE, STADIUM_NAME, ESTABLISHED_YEAR, NICKNAME, STADIUM_CAPACITY, COUNTRY_CODE, UPDATED_BY) " +
                "VALUES (:name, :teamType, :stadiumName, :establishedYear, :nickname, :stadiumCapacity, :countryCode, :updatedBy)";

        return new JdbcBatchItemWriterBuilder<TeamInput>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    // --- Step Configuration ---
    @Bean
    public Step teamLoadStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                             FlatFileItemReader<TeamInput> teamReader,
                             TeamItemProcessor teamProcessor,
                             JdbcBatchItemWriter<TeamInput> teamWriter) {
        return new StepBuilder("teamLoadStep", jobRepository)
                .<TeamInput, TeamInput>chunk(10, transactionManager)
                .reader(teamReader)
                .processor(teamProcessor)
                .writer(teamWriter)
                .build();
    }

    // --- Job Configuration ---
    @Bean
    public Job teamLoadJob(JobRepository jobRepository, Step teamLoadStep) {
        return new JobBuilder("teamLoadJob", jobRepository)
                .start(teamLoadStep)
                .build();
    }
}