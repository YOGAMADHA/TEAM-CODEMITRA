# User Manual & Step-by-Step Operator Guide

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. Demo User Accounts

All demo accounts share the password: `Password@123`

| Role | Username | Usage Scenario |
|---|---|---|
| Administrator | `admin` | Overall administrative oversight and settings |
| Scrutiny Officer | `scrutiny_officer` | Document scrutiny, deficiency management, AI review |
| EVC Coordinator | `evc_coordinator` | Committee assignment, date scheduling |
| Auditor | `auditor` | Read-only ledger inspection, hash-chain verification |
| Applicant (Engg) | `abc_applicant` | ABC Engineering College application (Scrutiny) |
| Applicant (Poly) | `xyz_applicant` | XYZ Polytechnic application (Deficiency open) |
| Applicant (Mgmt) | `pqr_applicant` | PQR Management application (EVC report pending) |

## 2. Navigating the Interface

### Applicant Workflows
1. Log in as `abc_applicant`.
2. Navigate to **Applications** ➔ View AICTE-2024-001.
3. Review checklist progress in the **Documents** tab.
4. Click **New Application** to initiate a draft application.

### Scrutiny Officer Workflows
1. Log in as `scrutiny_officer`.
2. Navigate to **Applications** ➔ Select application under `SCRUTINY`.
3. Open **Documents** tab ➔ Verify or reject documents with remarks.
4. Open **Deficiencies** tab ➔ Click **Raise Deficiency** to request corrective documentation.
5. Open **AI Assistance** tab ➔ Run **Generate AI Summary** or **Check Missing Documents** ➔ Click **Sign-off & Confirm Analysis**.

### Auditor Workflows
1. Log in as `auditor`.
2. Navigate to **Audit Trail**.
3. Apply filters by entity type, action, or date.
4. Click **Verify Hash Chain** to run the real-time cryptographic audit verification.
5. Click **Export CSV** to download the audit trail.
