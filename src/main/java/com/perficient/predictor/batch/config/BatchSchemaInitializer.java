package com.perficient.predictor.batch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class BatchSchemaInitializer {

    // Inject the path to the official H2 schema script provided by Spring Batch
    @Value("classpath:/org/springframework/batch/core/schema-h2.sql")
    private Resource batchScript;

    /**
     * Creates a DataSourceInitializer bean to force the execution of the batch schema script.
     * This runs before the JobRepository attempts to use the tables.
     */
    @Bean
    public DataSourceInitializer batchDataSourceInitializer(DataSource dataSource) {
        System.out.println("--- Forcing Spring Batch H2 Schema Initialization ---");

        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

        // 1. Add the specific schema script for H2
        databasePopulator.addScript(batchScript);

        // 2. Set to true to allow the script to drop existing tables without errors
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);

        // Ensure this initializer is enabled and runs on startup
        initializer.setEnabled(true);

        return initializer;
    }
}