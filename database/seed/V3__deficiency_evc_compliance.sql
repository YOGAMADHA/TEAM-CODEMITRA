-- Deficiencies
CREATE TABLE deficiencies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id),
    category VARCHAR(200),
    description TEXT NOT NULL,
    corrective_action TEXT,
    due_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'OPEN',
    supporting_document_requirement_id UUID REFERENCES document_requirements(id),
    raised_by_id UUID REFERENCES users(id),
    raised_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP,
    resolved_by_id UUID REFERENCES users(id),
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Deficiency Responses
CREATE TABLE deficiency_responses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deficiency_id UUID REFERENCES deficiencies(id),
    responded_by_id UUID REFERENCES users(id),
    response_text TEXT NOT NULL,
    supporting_document_id UUID REFERENCES documents(id),
    responded_at TIMESTAMP DEFAULT NOW(),
    reviewed_by_id UUID REFERENCES users(id),
    review_decision VARCHAR(20),
    review_remarks TEXT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- EVC Visits
CREATE TABLE evc_visits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id),
    status VARCHAR(50) DEFAULT 'EVC_REQUIRED',
    meeting_mode VARCHAR(20),
    proposed_date TIMESTAMP,
    confirmed_date TIMESTAMP,
    venue TEXT,
    rescheduling_reason TEXT,
    visit_completed_at TIMESTAMP,
    report_object_storage_key VARCHAR(1000),
    report_uploaded_at TIMESTAMP,
    report_uploaded_by_id UUID REFERENCES users(id),
    report_verified BOOLEAN DEFAULT FALSE,
    report_verified_by_id UUID REFERENCES users(id),
    report_verified_at TIMESTAMP,
    report_remarks TEXT,
    history_notes TEXT,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- EVC Members
CREATE TABLE evc_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    evc_visit_id UUID REFERENCES evc_visits(id),
    user_id UUID REFERENCES users(id),
    role VARCHAR(50),
    assigned_at TIMESTAMP DEFAULT NOW(),
    available BOOLEAN,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Compliance Items
CREATE TABLE compliance_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id),
    category VARCHAR(200),
    description TEXT NOT NULL,
    responsible_party VARCHAR(200),
    required_document_type VARCHAR(200),
    due_date TIMESTAMP,
    completed_date TIMESTAMP,
    verified BOOLEAN DEFAULT FALSE,
    verified_by_id UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    renewal_required BOOLEAN DEFAULT FALSE,
    renewal_date DATE,
    escalation_level VARCHAR(20),
    linked_document_id UUID REFERENCES documents(id),
    status VARCHAR(50) DEFAULT 'PENDING',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Undertakings
CREATE TABLE undertakings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID REFERENCES applications(id),
    undertaking_type VARCHAR(200),
    description TEXT,
    submitted_by_id UUID REFERENCES users(id),
    submitted_at TIMESTAMP DEFAULT NOW(),
    accepted BOOLEAN DEFAULT FALSE,
    accepted_at TIMESTAMP,
    document_id UUID,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_deficiencies_app ON deficiencies(application_id);
CREATE INDEX idx_deficiency_responses_def ON deficiency_responses(deficiency_id);
CREATE INDEX idx_evc_visits_app ON evc_visits(application_id);
CREATE INDEX idx_evc_members_visit ON evc_members(evc_visit_id);
CREATE INDEX idx_compliance_app ON compliance_items(application_id);
