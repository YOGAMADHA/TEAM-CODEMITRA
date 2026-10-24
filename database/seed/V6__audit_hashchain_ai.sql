-- V6: Add hash-chain fields to audit_logs and create ai_assistance table

-- Extend audit_logs table
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS user_id UUID;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS remarks TEXT;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS previous_hash VARCHAR(64);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS current_hash VARCHAR(64);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON audit_logs(performed_at);

-- AI Assistance table
CREATE TABLE IF NOT EXISTS ai_assistance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID,
    application_id UUID,
    requested_by VARCHAR(255),
    task_type VARCHAR(100) NOT NULL,
    model_name VARCHAR(100),
    prompt TEXT,
    output TEXT,
    confidence DOUBLE PRECISION,
    requested_at TIMESTAMP NOT NULL DEFAULT NOW(),
    human_confirmed BOOLEAN DEFAULT FALSE,
    human_confirmation_remarks TEXT,
    human_confirmed_at TIMESTAMP,
    human_confirmed_by VARCHAR(255),
    CONSTRAINT fk_ai_assistance_document FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE SET NULL,
    CONSTRAINT fk_ai_assistance_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_ai_assistance_application ON ai_assistance(application_id);
CREATE INDEX IF NOT EXISTS idx_ai_assistance_document ON ai_assistance(document_id);
CREATE INDEX IF NOT EXISTS idx_ai_assistance_task_type ON ai_assistance(task_type);
CREATE INDEX IF NOT EXISTS idx_ai_assistance_requested_at ON ai_assistance(requested_at);
