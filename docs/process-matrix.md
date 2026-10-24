# Process Matrix & Stage Authority

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

| Stage Name | Responsible Role | Allowed Actions | Next Valid Stages | SLA Target |
|---|---|---|---|---|
| `DRAFT` | `APPLICANT` | Edit, Upload, Submit | `SUBMITTED`, `WITHDRAWN` | Open |
| `SUBMITTED` | `SCRUTINY_OFFICER` | Initial Validation, Return | `INITIAL_VALIDATION`, `RETURNED` | 3 Days |
| `INITIAL_VALIDATION` | `SCRUTINY_OFFICER` | Detailed Scrutiny, Defect | `SCRUTINY`, `DEFICIENCY_RAISED` | 5 Days |
| `SCRUTINY` | `SCRUTINY_OFFICER` | Verify Docs, Raise Deficiency | `DEFICIENCY_RAISED`, `EVC_REQUIRED`, `COMMITTEE_REVIEW` | 15 Days |
| `DEFICIENCY_RAISED` | `APPLICANT` | Upload Corrective Docs | `APPLICANT_RESPONSE` | 15 Days |
| `APPLICANT_RESPONSE` | `SCRUTINY_OFFICER` | Re-scrutiny, Defect | `RESCRUTINY`, `DEFICIENCY_RAISED` | 7 Days |
| `RESCRUTINY` | `SCRUTINY_OFFICER` | Resolve Defect, Forward | `EVC_REQUIRED`, `COMMITTEE_REVIEW` | 7 Days |
| `EVC_REQUIRED` | `EVC_COORDINATOR` | Form Panel, Propose Date | `EVC_SCHEDULING` | 5 Days |
| `EVC_SCHEDULING` | `EVC_COORDINATOR` | Confirm Date, Notify Inst. | `EVC_CONDUCTED`, `RESCHEDULED` | 10 Days |
| `EVC_CONDUCTED` | `EVC_MEMBER` | Complete Visit | `EVC_REPORT_PENDING` | 1 Day |
| `EVC_REPORT_PENDING`| `EVC_MEMBER` | Upload Signed Report | `EVC_REPORT_UPLOADED` | 3 Days |
| `EVC_REPORT_UPLOADED`| `REGIONAL_OFFICER`| Verify Report, Remarks | `COMMITTEE_REVIEW` | 3 Days |
| `COMMITTEE_REVIEW` | `DIRECTOR` / Committee | Deliberate, Recommend | `COMPETENT_AUTHORITY_DECISION` | 10 Days |
| `COMPETENT_AUTHORITY_DECISION` | `COMPETENT_AUTHORITY` | Final Decision | `APPROVED`, `REJECTED`, `RETURNED` | 7 Days |
| `APPROVED` | All (Read Only) | View Approval Order | None (Terminal) | — |
| `REJECTED` | All (Read Only) | View Rejection Reasons | None (Terminal) | — |
| `WITHDRAWN` | All (Read Only) | Archive Record | None (Terminal) | — |
