-- Document Requirements (configurable per category)
CREATE TABLE document_requirements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(300) NOT NULL,
    description TEXT,
    application_category VARCHAR(200) NOT NULL,
    mandatory BOOLEAN DEFAULT TRUE,
    allowed_file_types VARCHAR(200) DEFAULT 'pdf,jpg,jpeg,png,docx',
    max_file_size_mb INTEGER DEFAULT 10,
    requires_expiry_date BOOLEAN DEFAULT FALSE,
    requires_human_verification BOOLEAN DEFAULT TRUE,
    reminder_config VARCHAR(200),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Documents
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id),
    document_requirement_id UUID REFERENCES document_requirements(id),
    file_name VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(500),
    object_storage_key VARCHAR(1000),
    content_type VARCHAR(100),
    file_size BIGINT,
    checksum VARCHAR(128),
    version_number INTEGER DEFAULT 1,
    issue_date DATE,
    expiry_date DATE,
    verification_status VARCHAR(50) DEFAULT 'NOT_REVIEWED',
    verification_remarks TEXT,
    verified_by_id UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    uploaded_by_id UUID REFERENCES users(id),
    uploaded_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN DEFAULT FALSE,
    replaced_document_id UUID REFERENCES documents(id),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_documents_app ON documents(application_id);
CREATE INDEX idx_documents_req ON documents(document_requirement_id);
CREATE INDEX idx_documents_checksum ON documents(checksum);
