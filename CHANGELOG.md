# Changelog

All notable changes to the AICTE Approval Workflow Management System will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-10-24

### Added
- **Core Workflow Engine**: Human-supervised stage progression from DRAFT to COMPETENT_AUTHORITY_DECISION.
- **Cryptographic Hash-Chain Audit Trail**: SHA-256 tamper-evident chaining across all administrative modifications with Auditor portal verification.
- **Document Management**: SHA-256 duplicate detection, file type & size validation, manual officer verification, versioning, soft deletion.
- **Deficiency Management**: Structured defect tracking, corrective actions, applicant response submissions, and officer review.
- **Expert Visit Committee (EVC)**: Inspection scheduling, member roster conflict detection, visit tracking, and report upload/verification.
- **Statutory Deadlines & Escalation Engine**: Automated reminders, deadline calculation, and background stage escalation.
- **AI-Assisted Features with Governance**: OCR text extraction, document classification suggestions, checklist gap detection, summary briefs, and parameterized natural language search with mandatory disclaimers.
- **Security Hardening**: Bucket4j login rate limiting, Spring Security JWT authentication, object-level access isolation, secure HTTP headers, error stack trace masking.
- **Analytical Reporting**: Pendency metrics, SLA overdue applications, stage-wise distribution, and CSV export.
- **Real-Time Polling**: 30-second TanStack / setInterval polling for notification count and dashboard stats.
- **Docker Compose Environment**: Complete orchestrations for PostgreSQL 16, MinIO, MailHog, backend, and frontend.
- **Automated Verification**: GitHub Actions CI workflows for backend testing, frontend build, and security scanning.
