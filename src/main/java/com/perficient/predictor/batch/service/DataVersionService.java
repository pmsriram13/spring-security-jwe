package com.perficient.predictor.batch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer responsible for managing the incremental data version counter
 * in the DATA_VERSION table. This replaces the direct DAO logic in the controller.
 */
@Service
public class DataVersionService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataVersionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Atomically retrieves the current version count for a given entity,
     * increments it by 1, and updates the counter in the database.
     * This method is marked @Transactional to ensure the read and write happen
     * together successfully.
     * * @param entityName The name of the entity/table being tracked (e.g., "TEAM").
     * @return The new, incremented version count.
     */
    @Transactional
    public long getAndIncrementVersion(String entityName) {
        // SQL for finding the current version count
        String selectSql = "SELECT VERSION_COUNT FROM DATA_VERSION WHERE ENTITY_NAME = ?";

        Long currentVersion;
        try {
            // 1. Read the current version count
            currentVersion = jdbcTemplate.queryForObject(selectSql, Long.class, entityName);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // If the row doesn't exist, start at 0
            currentVersion = 0L;
        }

        long newVersion = currentVersion + 1;

        // 2. Update the counter with the new version and timestamp
        String updateSql = "UPDATE DATA_VERSION SET VERSION_COUNT = ?, LAST_UPDATED = NOW() WHERE ENTITY_NAME = ?";
        int updatedRows = jdbcTemplate.update(updateSql, newVersion, entityName);

        // 3. If the update fails (row didn't exist), insert it instead
        if (updatedRows == 0) {
            String insertSql = "INSERT INTO DATA_VERSION (ENTITY_NAME, VERSION_COUNT, LAST_UPDATED) VALUES (?, ?, NOW())";
            jdbcTemplate.update(insertSql, entityName, newVersion);
        }

        return newVersion;
    }
}