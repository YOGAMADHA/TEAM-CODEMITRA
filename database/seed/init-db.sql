-- AICTE Workflow Management System - Database Initialization
-- Run this script if initializing PostgreSQL manually without Flyway

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE aicte_db TO aicte_user;
