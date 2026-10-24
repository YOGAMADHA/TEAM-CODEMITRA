# AICTE Approval Workflow Database Documentation

This directory contains the database initialization schemas, baseline Flyway migrations, and realistic demonstration seed sets for the AICTE Approval Workflow Management System.

## Structure
- `seed/`: SQL initialization scripts and baseline migrations (`V1` to `V7`).
- `sample-data/`: Representative JSON fixtures modeling institutions, applications, and documents for offline testing and data loading.

## Database Migrations
Migrations are managed automatically via **Flyway** on application startup:
- `V1__initial_schema.sql`: Core schema (users, roles, permissions, institutions, applications).
- `V2__document_tables.sql`: Document requirements, metadata, and checksums.
- `V3__deficiency_evc_compliance.sql`: Deficiencies, responses, EVC visits, members, and compliance items.
- `V4__deadline_notification_escalation.sql`: Deadlines, reminders, escalations, notifications, and initial audit logs.
- `V5__seed_data.sql`: 9 system roles, 24 granular permissions, workflow stages, and admin credentials.
- `V6__audit_hashchain_ai.sql`: Cryptographic hash-chain fields on audit logs and AI assistance tables.
- `V7__demo_seed_extended.sql`: Pre-seeded realistic demo data (3 institutions, 3 applications at different stages, deficiency, EVC visit, overdue deadline).
