# Database Design Documentation

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. Database Specifications
- **Engine**: PostgreSQL 16
- **Primary Keys**: UUID v4 (`gen_random_uuid()`)
- **Concurrency Control**: Optimistic Locking (`version` column via JPA `@Version`)
- **Migration Management**: Flyway migrations `V1` through `V7`

## 2. Table Classification & Schemas

### Identity & Access Control
- `roles`: Role definitions (APPLICANT, SCRUTINY_OFFICER, REGIONAL_OFFICER, EVC_MEMBER, etc.).
- `permissions`: 24 granular permissions.
- `role_permissions`: Join table mapping roles to permissions.
- `institutions`: Profile details, AISHE code, affiliation, address, contact coordinates.
- `users`: User credentials (BCrypt hashed), role reference, and optional institution link.
- `refresh_tokens`: Token rotation and revocation tracking.

### Core Workflow & Applications
- `applications`: Application number, institution reference, category, current stage, status, intake requested.
- `application_stage_history`: Historical log of all stage transitions, changed by, timestamp, and officer remarks.
- `assignments`: Reviewer assignments (e.g. Scrutiny Officer, Regional Officer).

### Documents & Requirements
- `document_requirements`: Category checklist rules, mandatory flag, maximum file size, allowed extensions.
- `documents`: Stored document metadata, MinIO storage key, SHA-256 checksum, verification status, remarks.

### Deficiencies & EVC Inspections
- `deficiencies`: Category, defect description, corrective action, due date, status.
- `deficiency_responses`: Applicant explanation, uploaded response document, officer resolution remarks.
- `evc_visits`: Proposed vs confirmed date, meeting mode (IN_PERSON/VIRTUAL/HYBRID), report status.
- `evc_members`: Assigned committee members with scheduling conflict checks.

### Audit & Governance
- `audit_logs`: Immutable ledger, entity type, entity ID, action, oldValue, newValue, SHA-256 previous and current hash.
- `ai_assistance`: Model name, prompt, output, confidence, human confirmation status, and officer sign-off remarks.
