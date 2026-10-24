-- Seed Roles
INSERT INTO roles (id, name, description) VALUES
    (uuid_generate_v4(), 'APPLICANT', 'Institution applicant who submits applications'),
    (uuid_generate_v4(), 'SCRUTINY_OFFICER', 'Performs initial scrutiny of applications'),
    (uuid_generate_v4(), 'RESCRUTINY_OFFICER', 'Performs re-scrutiny after deficiency resolution'),
    (uuid_generate_v4(), 'REGIONAL_OFFICER', 'Regional officer overseeing applications'),
    (uuid_generate_v4(), 'EVC_MEMBER', 'Expert Visiting Committee member'),
    (uuid_generate_v4(), 'COMMITTEE_MEMBER', 'Review committee member'),
    (uuid_generate_v4(), 'COMPETENT_AUTHORITY', 'Final decision-making authority'),
    (uuid_generate_v4(), 'SYSTEM_ADMINISTRATOR', 'System administrator with full configuration access'),
    (uuid_generate_v4(), 'AUDITOR', 'Read-only auditor');

-- Seed Permissions
INSERT INTO permissions (id, name, description) VALUES
    (uuid_generate_v4(), 'APPLICATION_CREATE', 'Create new applications'),
    (uuid_generate_v4(), 'APPLICATION_VIEW_OWN', 'View own applications'),
    (uuid_generate_v4(), 'APPLICATION_VIEW_ASSIGNED', 'View assigned applications'),
    (uuid_generate_v4(), 'APPLICATION_ASSIGN', 'Assign applications to officers'),
    (uuid_generate_v4(), 'APPLICATION_SUBMIT', 'Submit applications for review'),
    (uuid_generate_v4(), 'APPLICATION_ROUTE', 'Route applications between stages'),
    (uuid_generate_v4(), 'DOCUMENT_UPLOAD', 'Upload documents'),
    (uuid_generate_v4(), 'DOCUMENT_VIEW', 'View and download documents'),
    (uuid_generate_v4(), 'DOCUMENT_VERIFY', 'Verify uploaded documents'),
    (uuid_generate_v4(), 'DEFICIENCY_CREATE', 'Create deficiency records'),
    (uuid_generate_v4(), 'DEFICIENCY_RESPOND', 'Respond to deficiencies'),
    (uuid_generate_v4(), 'RESCRUTINY_PERFORM', 'Perform re-scrutiny'),
    (uuid_generate_v4(), 'EVC_SCHEDULE', 'Schedule EVC visits'),
    (uuid_generate_v4(), 'EVC_REPORT_UPLOAD', 'Upload EVC reports'),
    (uuid_generate_v4(), 'COMMITTEE_REVIEW', 'Participate in committee review'),
    (uuid_generate_v4(), 'FINAL_DECISION_RECORD', 'Record final approval/rejection decisions'),
    (uuid_generate_v4(), 'DEADLINE_CONFIGURE', 'Configure deadlines and reminders'),
    (uuid_generate_v4(), 'USER_MANAGE', 'Manage users'),
    (uuid_generate_v4(), 'WORKFLOW_CONFIGURE', 'Configure workflow settings'),
    (uuid_generate_v4(), 'AUDIT_VIEW', 'View audit logs'),
    (uuid_generate_v4(), 'REPORT_VIEW', 'View reports'),
    (uuid_generate_v4(), 'AI_ASSISTANCE_USE', 'Use AI assistance features'),
    (uuid_generate_v4(), 'COMPLIANCE_MANAGE', 'Manage compliance items'),
    (uuid_generate_v4(), 'ESCALATION_MANAGE', 'Manage escalations');

-- APPLICANT permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'APPLICANT' AND p.name IN (
    'APPLICATION_CREATE', 'APPLICATION_VIEW_OWN', 'APPLICATION_SUBMIT',
    'DOCUMENT_UPLOAD', 'DOCUMENT_VIEW', 'DEFICIENCY_RESPOND'
);

-- SCRUTINY_OFFICER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SCRUTINY_OFFICER' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'APPLICATION_ROUTE', 'DOCUMENT_VIEW',
    'DOCUMENT_VERIFY', 'DEFICIENCY_CREATE', 'REPORT_VIEW'
);

-- RESCRUTINY_OFFICER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'RESCRUTINY_OFFICER' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'APPLICATION_ROUTE', 'DOCUMENT_VIEW',
    'DOCUMENT_VERIFY', 'DEFICIENCY_CREATE', 'RESCRUTINY_PERFORM', 'REPORT_VIEW'
);

-- REGIONAL_OFFICER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'REGIONAL_OFFICER' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'APPLICATION_ASSIGN', 'APPLICATION_ROUTE',
    'DOCUMENT_VIEW', 'DOCUMENT_VERIFY', 'DEFICIENCY_CREATE',
    'EVC_SCHEDULE', 'REPORT_VIEW', 'ESCALATION_MANAGE'
);

-- EVC_MEMBER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'EVC_MEMBER' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'DOCUMENT_VIEW', 'EVC_REPORT_UPLOAD'
);

-- COMMITTEE_MEMBER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COMMITTEE_MEMBER' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'DOCUMENT_VIEW', 'COMMITTEE_REVIEW', 'REPORT_VIEW'
);

-- COMPETENT_AUTHORITY permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COMPETENT_AUTHORITY' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'APPLICATION_ROUTE', 'DOCUMENT_VIEW',
    'FINAL_DECISION_RECORD', 'REPORT_VIEW'
);

-- SYSTEM_ADMINISTRATOR permissions (all except final statutory decision)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'SYSTEM_ADMINISTRATOR' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'APPLICATION_ASSIGN', 'APPLICATION_ROUTE',
    'DOCUMENT_VIEW', 'DOCUMENT_VERIFY', 'DEFICIENCY_CREATE',
    'EVC_SCHEDULE', 'DEADLINE_CONFIGURE', 'USER_MANAGE',
    'WORKFLOW_CONFIGURE', 'AUDIT_VIEW', 'REPORT_VIEW',
    'AI_ASSISTANCE_USE', 'COMPLIANCE_MANAGE', 'ESCALATION_MANAGE'
);

-- AUDITOR permissions (read-only)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'AUDITOR' AND p.name IN (
    'APPLICATION_VIEW_ASSIGNED', 'DOCUMENT_VIEW', 'AUDIT_VIEW', 'REPORT_VIEW'
);

-- Seed Workflow Configurations for "New Institution Approval"
INSERT INTO workflow_configurations (id, name, category, from_stage, to_stage, required_permission, sla_hours, active) VALUES
    (uuid_generate_v4(), 'NIA: Draft to Submitted', 'New Institution Approval', 'DRAFT', 'SUBMITTED', 'APPLICATION_SUBMIT', NULL, true),
    (uuid_generate_v4(), 'NIA: Submitted to Validation', 'New Institution Approval', 'SUBMITTED', 'INITIAL_VALIDATION', 'APPLICATION_ROUTE', 48, true),
    (uuid_generate_v4(), 'NIA: Validation to Scrutiny', 'New Institution Approval', 'INITIAL_VALIDATION', 'SCRUTINY', 'APPLICATION_ROUTE', 72, true),
    (uuid_generate_v4(), 'NIA: Scrutiny to Deficiency', 'New Institution Approval', 'SCRUTINY', 'DEFICIENCY_RAISED', 'DEFICIENCY_CREATE', NULL, true),
    (uuid_generate_v4(), 'NIA: Scrutiny to EVC Required', 'New Institution Approval', 'SCRUTINY', 'EVC_REQUIRED', 'APPLICATION_ROUTE', NULL, true),
    (uuid_generate_v4(), 'NIA: Deficiency to Response', 'New Institution Approval', 'DEFICIENCY_RAISED', 'APPLICANT_RESPONSE', 'DEFICIENCY_RESPOND', 336, true),
    (uuid_generate_v4(), 'NIA: Response to Rescrutiny', 'New Institution Approval', 'APPLICANT_RESPONSE', 'RESCRUTINY', 'RESCRUTINY_PERFORM', 72, true),
    (uuid_generate_v4(), 'NIA: Rescrutiny to EVC', 'New Institution Approval', 'RESCRUTINY', 'EVC_REQUIRED', 'APPLICATION_ROUTE', NULL, true),
    (uuid_generate_v4(), 'NIA: Rescrutiny to Deficiency', 'New Institution Approval', 'RESCRUTINY', 'DEFICIENCY_RAISED', 'DEFICIENCY_CREATE', NULL, true),
    (uuid_generate_v4(), 'NIA: EVC Required to Scheduling', 'New Institution Approval', 'EVC_REQUIRED', 'EVC_SCHEDULING', 'EVC_SCHEDULE', 168, true),
    (uuid_generate_v4(), 'NIA: EVC Scheduling to Conducted', 'New Institution Approval', 'EVC_SCHEDULING', 'EVC_CONDUCTED', 'EVC_SCHEDULE', NULL, true),
    (uuid_generate_v4(), 'NIA: EVC Conducted to Report Pending', 'New Institution Approval', 'EVC_CONDUCTED', 'EVC_REPORT_PENDING', 'APPLICATION_ROUTE', 48, true),
    (uuid_generate_v4(), 'NIA: Report Pending to Uploaded', 'New Institution Approval', 'EVC_REPORT_PENDING', 'EVC_REPORT_UPLOADED', 'EVC_REPORT_UPLOAD', 336, true),
    (uuid_generate_v4(), 'NIA: Report Uploaded to Committee', 'New Institution Approval', 'EVC_REPORT_UPLOADED', 'COMMITTEE_REVIEW', 'APPLICATION_ROUTE', 168, true),
    (uuid_generate_v4(), 'NIA: Committee to Authority', 'New Institution Approval', 'COMMITTEE_REVIEW', 'COMPETENT_AUTHORITY_DECISION', 'COMMITTEE_REVIEW', 336, true),
    (uuid_generate_v4(), 'NIA: Authority to Approved', 'New Institution Approval', 'COMPETENT_AUTHORITY_DECISION', 'APPROVED', 'FINAL_DECISION_RECORD', NULL, true),
    (uuid_generate_v4(), 'NIA: Authority to Rejected', 'New Institution Approval', 'COMPETENT_AUTHORITY_DECISION', 'REJECTED', 'FINAL_DECISION_RECORD', NULL, true),
    (uuid_generate_v4(), 'NIA: Authority to Returned', 'New Institution Approval', 'COMPETENT_AUTHORITY_DECISION', 'RETURNED', 'FINAL_DECISION_RECORD', NULL, true);

-- Configurations for "Extension or Renewal of Approval"
INSERT INTO workflow_configurations (id, name, category, from_stage, to_stage, required_permission, sla_hours, active)
SELECT uuid_generate_v4(), REPLACE(name, 'NIA:', 'ERA:'), 'Extension or Renewal of Approval', from_stage, to_stage, required_permission, sla_hours, active
FROM workflow_configurations WHERE category = 'New Institution Approval';

-- Configurations for "Additional Course or Intake Approval"
INSERT INTO workflow_configurations (id, name, category, from_stage, to_stage, required_permission, sla_hours, active)
SELECT uuid_generate_v4(), REPLACE(name, 'NIA:', 'ACI:'), 'Additional Course or Intake Approval', from_stage, to_stage, required_permission, sla_hours, active
FROM workflow_configurations WHERE category = 'New Institution Approval';

-- Seed Document Requirements for "New Institution Approval"
INSERT INTO document_requirements (id, name, description, application_category, mandatory, allowed_file_types, max_file_size_mb, requires_expiry_date, requires_human_verification, active) VALUES
    (uuid_generate_v4(), 'Trust/Society Registration Certificate', 'Registration certificate of the trust or society', 'New Institution Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Land Documents', 'Proof of land ownership or lease agreement', 'New Institution Approval', true, 'pdf', 10, true, true, true),
    (uuid_generate_v4(), 'Building Plan Approval', 'Approved building plan from local authority', 'New Institution Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Financial Statements', 'Audited financial statements of the trust/society', 'New Institution Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Faculty List', 'List of proposed faculty with qualifications', 'New Institution Approval', true, 'pdf,docx', 10, false, true, true),
    (uuid_generate_v4(), 'NOC from State Government', 'No Objection Certificate from state government', 'New Institution Approval', true, 'pdf', 10, true, true, true),
    (uuid_generate_v4(), 'Affiliation Letter', 'Letter of affiliation from university', 'New Institution Approval', false, 'pdf', 10, true, true, true),
    (uuid_generate_v4(), 'Infrastructure Photos', 'Photos of institution infrastructure', 'New Institution Approval', false, 'jpg,jpeg,png', 10, false, false, true);

-- Document Requirements for "Extension or Renewal of Approval"
INSERT INTO document_requirements (id, name, description, application_category, mandatory, allowed_file_types, max_file_size_mb, requires_expiry_date, requires_human_verification, active) VALUES
    (uuid_generate_v4(), 'Previous Approval Letter', 'AICTE approval letter from previous year', 'Extension or Renewal of Approval', true, 'pdf', 10, true, true, true),
    (uuid_generate_v4(), 'Compliance Report', 'Compliance report for current approval period', 'Extension or Renewal of Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Updated Faculty List', 'Current faculty list with qualifications', 'Extension or Renewal of Approval', true, 'pdf,docx', 10, false, true, true),
    (uuid_generate_v4(), 'Financial Statements', 'Latest audited financial statements', 'Extension or Renewal of Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Student Enrollment Data', 'Enrollment data for existing courses', 'Extension or Renewal of Approval', true, 'pdf,docx', 10, false, false, true);

-- Document Requirements for "Additional Course or Intake Approval"
INSERT INTO document_requirements (id, name, description, application_category, mandatory, allowed_file_types, max_file_size_mb, requires_expiry_date, requires_human_verification, active) VALUES
    (uuid_generate_v4(), 'Course Syllabus', 'Proposed course syllabus', 'Additional Course or Intake Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'Market Survey Report', 'Market survey justifying new course/intake', 'Additional Course or Intake Approval', true, 'pdf', 10, false, false, true),
    (uuid_generate_v4(), 'Additional Faculty Details', 'Details of additional faculty to be hired', 'Additional Course or Intake Approval', true, 'pdf,docx', 10, false, true, true),
    (uuid_generate_v4(), 'Infrastructure Augmentation Plan', 'Plan for additional infrastructure', 'Additional Course or Intake Approval', true, 'pdf', 10, false, true, true),
    (uuid_generate_v4(), 'University Affiliation for New Course', 'Affiliation letter for the new course', 'Additional Course or Intake Approval', false, 'pdf', 10, true, true, true);

-- Seed Notification Templates
INSERT INTO notification_templates (id, type, subject_template, body_template) VALUES
    (uuid_generate_v4(), 'APPLICATION_SUBMITTED', 'Application Submitted: {applicationNumber}', 'Your application {applicationNumber} has been successfully submitted and is now under review.'),
    (uuid_generate_v4(), 'STAGE_CHANGED', 'Application {applicationNumber} Stage Update', 'Application {applicationNumber} has moved to stage: {stage}. {remarks}'),
    (uuid_generate_v4(), 'DEFICIENCY_RAISED', 'Deficiency Raised: {applicationNumber}', 'A deficiency has been identified in your application {applicationNumber}. Category: {category}. Description: {description}. Please respond by {dueDate}.'),
    (uuid_generate_v4(), 'DEFICIENCY_RESPONSE', 'Deficiency Response Received: {applicationNumber}', 'A response has been submitted for deficiency in application {applicationNumber}.'),
    (uuid_generate_v4(), 'DEADLINE_REMINDER', 'Deadline Approaching: {description}', 'Reminder: The deadline for "{description}" related to application {applicationNumber} is {dueDate}.'),
    (uuid_generate_v4(), 'ESCALATION_ALERT', 'Escalation Alert: Level {level}', 'An escalation of level {level} has been triggered for application {applicationNumber}. Reason: {reason}'),
    (uuid_generate_v4(), 'DOCUMENT_UPLOADED', 'Document Uploaded: {documentName}', 'A new document "{documentName}" has been uploaded for application {applicationNumber}.'),
    (uuid_generate_v4(), 'DOCUMENT_VERIFIED', 'Document Verified: {documentName}', 'Document "{documentName}" for application {applicationNumber} has been verified.'),
    (uuid_generate_v4(), 'DOCUMENT_REJECTED', 'Document Rejected: {documentName}', 'Document "{documentName}" for application {applicationNumber} has been rejected. Remarks: {remarks}'),
    (uuid_generate_v4(), 'EVC_SCHEDULED', 'EVC Visit Scheduled: {applicationNumber}', 'An EVC visit has been scheduled for application {applicationNumber} on {date} at {venue}.'),
    (uuid_generate_v4(), 'EVC_REPORT_DUE', 'EVC Report Due: {applicationNumber}', 'The EVC report for application {applicationNumber} is due. Please upload it promptly.'),
    (uuid_generate_v4(), 'ASSIGNMENT_NOTIFICATION', 'New Application Assigned', 'You have been assigned application {applicationNumber}. Please review it at your earliest convenience.'),
    (uuid_generate_v4(), 'PASSWORD_RESET', 'Password Reset Request', 'A password reset has been requested for your account. Use token: {token} to reset your password. This link expires in 24 hours.'),
    (uuid_generate_v4(), 'ACCOUNT_ACTIVATION', 'Account Activated', 'Your account has been activated. You can now log in to the AICTE Workflow Management System.');

-- Seed default users (password: Admin@123 - BCrypt hashed: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy)
INSERT INTO users (id, email, password, first_name, last_name, active, email_verified) VALUES
    (uuid_generate_v4(), 'admin@aicte.gov.in', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System', 'Administrator', true, true);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@aicte.gov.in' AND r.name = 'SYSTEM_ADMINISTRATOR';

INSERT INTO users (id, email, password, first_name, last_name, active, email_verified) VALUES
    (uuid_generate_v4(), 'applicant@demo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo', 'Applicant', true, true);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'applicant@demo.com' AND r.name = 'APPLICANT';

INSERT INTO users (id, email, password, first_name, last_name, active, email_verified) VALUES
    (uuid_generate_v4(), 'officer@aicte.gov.in', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Demo', 'Officer', true, true);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'officer@aicte.gov.in' AND r.name = 'SCRUTINY_OFFICER';
