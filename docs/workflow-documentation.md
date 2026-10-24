# Workflow Lifecycle & State Machine Documentation

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. Lifecycle Stages

1. `DRAFT`: Initial applicant data entry; files and intake details can be edited freely.
2. `SUBMITTED`: Formally submitted by applicant; locked against modifications.
3. `INITIAL_VALIDATION`: Preliminary automated and clerical check for completeness.
4. `SCRUTINY`: Detailed document scrutiny by assigned Scrutiny Officer.
5. `DEFICIENCY_RAISED`: Official defects raised; countdown timer starts for applicant response.
6. `APPLICANT_RESPONSE`: Response submitted with corrective documentation.
7. `RESCRUTINY`: Re-scrutiny by officer to resolve or reject responses.
8. `EVC_REQUIRED`: Inspection mandated by policy rules or scrutiny recommendation.
9. `EVC_SCHEDULING`: Committee formed, date proposed and confirmed.
10. `EVC_CONDUCTED`: Physical / virtual inspection completed by committee.
11. `EVC_REPORT_PENDING`: Awaiting formal observation report from committee.
12. `EVC_REPORT_UPLOADED`: Report uploaded and validated.
13. `COMMITTEE_REVIEW`: Regional / Standing Scrutiny Committee evaluates holistic findings.
14. `COMPETENT_AUTHORITY_DECISION`: Council / Executive Committee issues final decision.
15. `APPROVED`: Terminal approval recorded with official letter reference.
16. `REJECTED`: Terminal rejection recorded with statutory grounds.
17. `RETURNED`: Returned to applicant for resubmission.
18. `WITHDRAWN`: Voluntarily withdrawn by applicant.

## 2. Transition Guard Rails
- Stage jumps are strictly prohibited (e.g. `DRAFT` cannot jump to `APPROVED`).
- Open deficiencies block progression to `COMMITTEE_REVIEW`.
- AI outputs cannot trigger stage transitions; human officer authentication is mandatory.
