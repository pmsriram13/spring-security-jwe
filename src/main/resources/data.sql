--
-- DML for the Spring Batch Soccer Points Application
-- Initial master data and mock transactional data for batch testing.
--
-- Note: This data is designed to run automatically by Spring Boot
-- when using the in-memory H2 configuration defined in application.properties.
--

-- Default user for audit columns
SET @updated_by = 'SYSTEM_INIT';

-- -------------------------
-- 1. TEAM Data
-- -------------------------


-- -------------------------
-- 2. COMPETITION Data
-- -------------------------

-- COMPETITION_ID 1-3

-- ------------------------
-- 4. PLAYER Data (PLAYER_ID 1-3)
-- -------------------------

INSERT INTO PLAYER (USER_NAME, EMAIL, PASSWORD_HASH, PASSWORD_EXPIRY_DATE, LAST_LOGIN_DATE, UPDATED_BY)
VALUES
    ('jdoe', 'john.doe@example.com', '$2a$10$hashed_password_A', DATEADD('YEAR', 1, CURRENT_TIMESTAMP()), DATEADD('DAY', -2, CURRENT_TIMESTAMP()), @updated_by),
    ('asmith', 'alice.smith@example.com', '$2a$10$hashed_password_B', DATEADD('YEAR', 1, CURRENT_TIMESTAMP()), DATEADD('HOUR', -5, CURRENT_TIMESTAMP()), @updated_by),
    ('testuser', 'test@user.com', '$2a$10$hashed_password_C', DATEADD('YEAR', 1, CURRENT_TIMESTAMP()), DATEADD('DAY', -10, CURRENT_TIMESTAMP()), @updated_by);


