-- V7: Extended demo seed with realistic scenario data

-- Demo institutions (if not already present from V5)
INSERT INTO institutions (id, name, code, address, city, state, pincode, phone, email, website, type, status, created_at, updated_at)
VALUES
    ('a1111111-1111-1111-1111-111111111111', 'ABC Engineering College', 'ABC-ENG-001', '123 College Road', 'Mumbai', 'Maharashtra', '400001', '022-12345678', 'principal@abc.edu', 'https://abc.edu', 'ENGINEERING', 'ACTIVE', NOW(), NOW()),
    ('a2222222-2222-2222-2222-222222222222', 'XYZ Polytechnic Institute', 'XYZ-POLY-001', '456 Institute Avenue', 'Pune', 'Maharashtra', '411001', '020-87654321', 'director@xyz.edu', 'https://xyz.edu', 'POLYTECHNIC', 'ACTIVE', NOW(), NOW()),
    ('a3333333-3333-3333-3333-333333333333', 'PQR Management Institute', 'PQR-MBA-001', '789 Management Street', 'Nagpur', 'Maharashtra', '440001', '0712-11111111', 'dean@pqr.edu', 'https://pqr.edu', 'MANAGEMENT', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Demo applicant users for the institutions
INSERT INTO users (id, username, email, password_hash, full_name, phone, role_id, institution_id, is_active, is_email_verified, created_at, updated_at)
SELECT
    'b1111111-1111-1111-1111-111111111111',
    'abc_applicant',
    'applicant@abc.edu',
    '$2a$12$LQv3c1yqBwlFBKc1y9eFX.q4K5RCSY2l4mJ9hJfRuZXmSXGzrWNZa',
    'Rajesh Kumar',
    '9876543210',
    r.id,
    'a1111111-1111-1111-1111-111111111111',
    TRUE, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'APPLICANT'
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, email, password_hash, full_name, phone, role_id, institution_id, is_active, is_email_verified, created_at, updated_at)
SELECT
    'b2222222-2222-2222-2222-222222222222',
    'xyz_applicant',
    'applicant@xyz.edu',
    '$2a$12$LQv3c1yqBwlFBKc1y9eFX.q4K5RCSY2l4mJ9hJfRuZXmSXGzrWNZa',
    'Priya Sharma',
    '9876543211',
    r.id,
    'a2222222-2222-2222-2222-222222222222',
    TRUE, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'APPLICANT'
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, email, password_hash, full_name, phone, role_id, institution_id, is_active, is_email_verified, created_at, updated_at)
SELECT
    'b3333333-3333-3333-3333-333333333333',
    'pqr_applicant',
    'applicant@pqr.edu',
    '$2a$12$LQv3c1yqBwlFBKc1y9eFX.q4K5RCSY2l4mJ9hJfRuZXmSXGzrWNZa',
    'Amit Patel',
    '9876543212',
    r.id,
    'a3333333-3333-3333-3333-333333333333',
    TRUE, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'APPLICANT'
ON CONFLICT (id) DO NOTHING;

-- Demo applications at different stages
INSERT INTO applications (id, application_number, institution_id, category, current_stage, status, submitted_by, submitted_at, academic_year, program_name, intake_requested, remarks, created_at, updated_at)
VALUES
    ('c1111111-1111-1111-1111-111111111111',
     'AICTE-2024-001',
     'a1111111-1111-1111-1111-111111111111',
     'NEW_INSTITUTION',
     'SCRUTINY',
     'IN_PROGRESS',
     'b1111111-1111-1111-1111-111111111111',
     NOW() - INTERVAL '30 days',
     '2024-25',
     'B.Tech Computer Science',
     120,
     'Application for new engineering college',
     NOW() - INTERVAL '30 days',
     NOW()),

    ('c2222222-2222-2222-2222-222222222222',
     'AICTE-2024-002',
     'a2222222-2222-2222-2222-222222222222',
     'EXTENSION_OF_APPROVAL',
     'DEFICIENCY_RAISED',
     'IN_PROGRESS',
     'b2222222-2222-2222-2222-222222222222',
     NOW() - INTERVAL '45 days',
     '2024-25',
     'Diploma in Mechanical Engineering',
     60,
     'Extension of approval for existing polytechnic',
     NOW() - INTERVAL '45 days',
     NOW()),

    ('c3333333-3333-3333-3333-333333333333',
     'AICTE-2024-003',
     'a3333333-3333-3333-3333-333333333333',
     'INCREASE_IN_INTAKE',
     'EVC_REPORT_PENDING',
     'IN_PROGRESS',
     'b3333333-3333-3333-3333-333333333333',
     NOW() - INTERVAL '60 days',
     '2024-25',
     'MBA General Management',
     60,
     'Increase in intake for MBA program',
     NOW() - INTERVAL '60 days',
     NOW())
ON CONFLICT (id) DO NOTHING;

-- Stage history for application 1
INSERT INTO application_stage_history (id, application_id, stage, remarks, changed_by, changed_at)
VALUES
    (gen_random_uuid(), 'c1111111-1111-1111-1111-111111111111', 'SUBMITTED', 'Application submitted', 'b1111111-1111-1111-1111-111111111111', NOW() - INTERVAL '30 days'),
    (gen_random_uuid(), 'c1111111-1111-1111-1111-111111111111', 'INITIAL_VALIDATION', 'Initial validation completed', 'scrutiny_officer', NOW() - INTERVAL '25 days'),
    (gen_random_uuid(), 'c1111111-1111-1111-1111-111111111111', 'SCRUTINY', 'Under detailed scrutiny', 'scrutiny_officer', NOW() - INTERVAL '20 days')
ON CONFLICT DO NOTHING;

-- Stage history for application 2
INSERT INTO application_stage_history (id, application_id, stage, remarks, changed_by, changed_at)
VALUES
    (gen_random_uuid(), 'c2222222-2222-2222-2222-222222222222', 'SUBMITTED', 'Application submitted', 'b2222222-2222-2222-2222-222222222222', NOW() - INTERVAL '45 days'),
    (gen_random_uuid(), 'c2222222-2222-2222-2222-222222222222', 'INITIAL_VALIDATION', 'Initial validation done', 'scrutiny_officer', NOW() - INTERVAL '40 days'),
    (gen_random_uuid(), 'c2222222-2222-2222-2222-222222222222', 'SCRUTINY', 'Scrutiny initiated', 'scrutiny_officer', NOW() - INTERVAL '35 days'),
    (gen_random_uuid(), 'c2222222-2222-2222-2222-222222222222', 'DEFICIENCY_RAISED', 'Deficiencies found in documents', 'scrutiny_officer', NOW() - INTERVAL '20 days')
ON CONFLICT DO NOTHING;

-- Stage history for application 3
INSERT INTO application_stage_history (id, application_id, stage, remarks, changed_by, changed_at)
VALUES
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'SUBMITTED', 'Application submitted', 'b3333333-3333-3333-3333-333333333333', NOW() - INTERVAL '60 days'),
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'INITIAL_VALIDATION', 'Validated', 'scrutiny_officer', NOW() - INTERVAL '55 days'),
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'SCRUTINY', 'Scrutiny done', 'scrutiny_officer', NOW() - INTERVAL '50 days'),
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'EVC_SCHEDULED', 'EVC visit scheduled', 'evc_coordinator', NOW() - INTERVAL '30 days'),
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'EVC_CONDUCTED', 'EVC visit conducted', 'evc_coordinator', NOW() - INTERVAL '10 days'),
    (gen_random_uuid(), 'c3333333-3333-3333-3333-333333333333', 'EVC_REPORT_PENDING', 'Awaiting EVC report upload', 'evc_coordinator', NOW() - INTERVAL '10 days')
ON CONFLICT DO NOTHING;

-- Deficiency for application 2
INSERT INTO deficiencies (id, application_id, category, description, required_action, due_date, status, created_by, created_at, updated_at)
VALUES
    ('d1111111-1111-1111-1111-111111111111',
     'c2222222-2222-2222-2222-222222222222',
     'DOCUMENT',
     'Fire safety certificate is missing or expired. The submitted certificate expired on 01-Jan-2024.',
     'Please upload a valid fire safety certificate issued after 01-Apr-2024 from the competent authority.',
     NOW() + INTERVAL '7 days',
     'OPEN',
     'scrutiny_officer',
     NOW() - INTERVAL '20 days',
     NOW() - INTERVAL '20 days')
ON CONFLICT (id) DO NOTHING;

-- Overdue deadline for application 1
INSERT INTO deadlines (id, application_id, stage, deadline_type, responsible_role, start_date, due_date, status, created_at, updated_at)
VALUES
    ('e1111111-1111-1111-1111-111111111111',
     'c1111111-1111-1111-1111-111111111111',
     'SCRUTINY',
     'STAGE_COMPLETION',
     'SCRUTINY_OFFICER',
     NOW() - INTERVAL '20 days',
     NOW() - INTERVAL '1 day',
     'OVERDUE',
     NOW() - INTERVAL '20 days',
     NOW())
ON CONFLICT (id) DO NOTHING;

-- EVC visit for application 3
INSERT INTO evc_visits (id, application_id, status, proposed_date, confirmed_date, meeting_mode, created_at, updated_at)
VALUES
    ('f1111111-1111-1111-1111-111111111111',
     'c3333333-3333-3333-3333-333333333333',
     'REPORT_PENDING',
     NOW() - INTERVAL '15 days',
     NOW() - INTERVAL '15 days',
     'IN_PERSON',
     NOW() - INTERVAL '30 days',
     NOW() - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;

-- Demo notifications
INSERT INTO notifications (id, user_id, type, title, message, is_read, application_id, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    'DEFICIENCY_RAISED',
    'Deficiency Raised on Your Application',
    'A deficiency has been raised on application AICTE-2024-002. Please respond by the due date.',
    FALSE,
    'c2222222-2222-2222-2222-222222222222',
    NOW() - INTERVAL '20 days'
FROM users u WHERE u.username = 'xyz_applicant'
ON CONFLICT DO NOTHING;

INSERT INTO notifications (id, user_id, type, title, message, is_read, application_id, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    'EVC_REPORT_PENDING',
    'EVC Report Upload Pending',
    'EVC visit for application AICTE-2024-003 was completed on ' || (NOW() - INTERVAL '10 days')::date || '. Please upload the EVC report.',
    FALSE,
    'c3333333-3333-3333-3333-333333333333',
    NOW() - INTERVAL '10 days'
FROM users u WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO notifications (id, user_id, type, title, message, is_read, application_id, created_at)
SELECT
    gen_random_uuid(),
    u.id,
    'DEADLINE_OVERDUE',
    'Scrutiny Deadline Overdue',
    'The scrutiny deadline for application AICTE-2024-001 was due yesterday. Immediate action required.',
    FALSE,
    'c1111111-1111-1111-1111-111111111111',
    NOW() - INTERVAL '1 day'
FROM users u WHERE u.username = 'admin'
ON CONFLICT DO NOTHING;

-- Demo audit log entries
INSERT INTO audit_logs (id, entity_type, entity_id, action, performed_by, performed_at, new_value, ip_address)
VALUES
    (gen_random_uuid(), 'APPLICATION', 'c1111111-1111-1111-1111-111111111111', 'APPLICATION_CREATED', 'abc_applicant', NOW() - INTERVAL '30 days', '{"applicationNumber":"AICTE-2024-001","stage":"SUBMITTED"}', '192.168.1.100'),
    (gen_random_uuid(), 'APPLICATION', 'c2222222-2222-2222-2222-222222222222', 'APPLICATION_CREATED', 'xyz_applicant', NOW() - INTERVAL '45 days', '{"applicationNumber":"AICTE-2024-002","stage":"SUBMITTED"}', '192.168.1.101'),
    (gen_random_uuid(), 'APPLICATION', 'c3333333-3333-3333-3333-333333333333', 'APPLICATION_CREATED', 'pqr_applicant', NOW() - INTERVAL '60 days', '{"applicationNumber":"AICTE-2024-003","stage":"SUBMITTED"}', '192.168.1.102'),
    (gen_random_uuid(), 'DEFICIENCY', 'd1111111-1111-1111-1111-111111111111', 'DEFICIENCY_CREATED', 'scrutiny_officer', NOW() - INTERVAL '20 days', '{"status":"OPEN","category":"DOCUMENT"}', '10.0.0.1'),
    (gen_random_uuid(), 'EVC_VISIT', 'f1111111-1111-1111-1111-111111111111', 'EVC_CONDUCTED', 'evc_coordinator', NOW() - INTERVAL '10 days', '{"status":"REPORT_PENDING"}', '10.0.0.2')
ON CONFLICT DO NOTHING;
