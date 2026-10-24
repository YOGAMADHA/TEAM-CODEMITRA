# Role & Permission Matrix

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## System Roles & Responsibilities

1. **`APPLICANT`**: Authorized representative of an affiliated institution.
2. **`SCRUTINY_OFFICER`**: Verifies documents, issues deficiency notes, and conducts initial checks.
3. **`REGIONAL_OFFICER`**: Supervises scrutiny officers, manages regional workflow, handles escalations.
4. **`EVC_COORDINATOR`**: Manages EVC rosters, schedules visits, detects member scheduling conflicts.
5. **`EVC_MEMBER`**: Domain expert inspector; conducts physical/virtual visits and uploads reports.
6. **`DIRECTOR` / Committee Member**: Evaluates committee review stage and holistic compliance.
7. **`COMPETENT_AUTHORITY`**: Issues final statutory decisions (Approval/Rejection).
8. **`AUDITOR`**: Independent vigilance; read-only access to records and cryptographic hash verification.
9. **`SYSTEM_ADMINISTRATOR` (ADMIN)**: System configuration, user provisioning, audit inspection.

## Granular Permission Mapping

| Permission | APPLICANT | SCRUTINY_OFFICER | REGIONAL_OFFICER | EVC_COORDINATOR | EVC_MEMBER | DIRECTOR | COMPETENT_AUTH | AUDITOR | ADMIN |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| `APPLICATION_CREATE` | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `APPLICATION_SUBMIT` | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `APPLICATION_VIEW_OWN` | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| `APPLICATION_VIEW_ALL` | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `APPLICATION_ROUTE` | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ | ❌ | ✅ |
| `APPLICATION_DECIDE` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ |
| `DOCUMENT_UPLOAD` | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `DOCUMENT_DOWNLOAD` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `DOCUMENT_VERIFY` | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `DEFICIENCY_CREATE` | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `DEFICIENCY_RESPOND` | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| `DEFICIENCY_REVIEW` | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `EVC_SCHEDULE` | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `EVC_CONDUCT` | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |
| `EVC_REPORT_UPLOAD` | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |
| `EVC_REPORT_VERIFY` | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `COMPLIANCE_MANAGE` | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `DEADLINE_MANAGE` | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `ESCALATION_MANAGE` | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ |
| `REPORT_VIEW` | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ |
| `USER_MANAGE` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| `AUDIT_VIEW` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| `AUDIT_VERIFY` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| `TIME_SIMULATE` | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
