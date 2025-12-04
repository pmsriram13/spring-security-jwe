package com.perficient.predictor.batch.config;

import com.perficient.predictor.batch.dto.CountryCsvInput;
import com.perficient.predictor.batch.dto.CountryDBOutput;
import com.perficient.predictor.batch.exception.ValidationException;
import com.perficient.predictor.batch.processor.CountryItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;

/**
 * Configures the Spring Batch job for loading Country master data.
 */
@Configuration
public class CountryLoadConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public CountryLoadConfig(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    // --- Reader Configuration ---
    @Bean
    @StepScope
    public FlatFileItemReader<CountryCsvInput> countryReader(
            @Value("#{jobParameters['inputFilePath']}") String inputFilePath) {
// Add the "file:" prefix if it's missing, to force filesystem resolution
        String safePath = inputFilePath.startsWith("file:") ? inputFilePath : "file:" + inputFilePath;
        // You now need to convert the String path into a Spring Resource manually
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(safePath);


        return new FlatFileItemReaderBuilder<CountryCsvInput>()
                .name("countryItemReader")
                .resource(resource)
                .delimited()
                .names("countryId", "countryName")
                .targetType(CountryCsvInput.class)
                .linesToSkip(1) // Skip header row
                .build();
    }

    // --- Processor Configuration ---
    @Bean
    public CountryItemProcessor countryProcessor() {
        return new CountryItemProcessor();
    }

    // --- Writer Configuration ---
    @Bean
    public JdbcBatchItemWriter<CountryDBOutput> countryWriter() {

        final String upsertSql = """
        MERGE INTO COUNTRY 
        (NAME, CODE, UPDATED_BY)
        KEY (CODE) 
        VALUES 
        (:code, :name, :updatedBy)
        """;

        return new JdbcBatchItemWriterBuilder<CountryDBOutput>()
                .dataSource(this.dataSource)
                .sql(upsertSql)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    // --- Step Configuration ---
    @Bean
    public Step countryLoadStep(FlatFileItemReader<CountryCsvInput> countryReader,
                                CountryItemProcessor countryProcessor,
                                JdbcBatchItemWriter<CountryDBOutput> countryWriter) {

        return new StepBuilder("countryLoadStep", jobRepository)
                .<CountryCsvInput, CountryDBOutput>chunk(10, transactionManager)
                .reader(countryReader)
                .processor(countryProcessor)
                .writer(countryWriter)
                .faultTolerant()
                .skipLimit(10)
                .skip(ValidationException.class)
                .skip(DataAccessException.class)
                .build();
    }

    // --- Job Configuration ---
    @Bean
    public Job countryLoadJob(Step countryLoadStep) {
        return new JobBuilder("countryLoadJob", jobRepository)
                .start(countryLoadStep)
                .build();
    }
}