# API Documentation & Endpoint Reference

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

Interactive Swagger / OpenAPI UI: `http://localhost:8080/swagger-ui.html`

## 1. Authentication (`/api/auth`)
- `POST /api/auth/login`: Authenticate credentials (rate limited to 5 requests/min per IP). Returns access & refresh tokens.
- `POST /api/auth/refresh`: Exchange valid refresh token for new access token.
- `POST /api/auth/logout`: Revoke active session token.
- `POST /api/auth/change-password`: Change user password with BCrypt hashing.

## 2. Applications (`/api/applications`)
- `GET /api/applications`: List applications (scoped to own institution for applicants, all for officers).
- `POST /api/applications`: Create draft application.
- `GET /api/applications/{id}`: Detailed application overview with documents and timeline.
- `POST /api/applications/{id}/submit`: Formally submit draft application.
- `POST /api/applications/{id}/transition`: Transition workflow stage (requires `APPLICATION_ROUTE`).
- `POST /api/applications/{id}/assign`: Assign scrutiny or regional officer.

## 3. Documents (`/api/documents`)
- `GET /api/documents/checklist/{applicationId}`: Category document checklist progress.
- `POST /api/documents/upload`: Upload file with SHA-256 deduplication and MIME verification.
- `GET /api/documents/download/{id}`: Download document binary.
- `POST /api/documents/{id}/verify`: Human officer verification with remarks.
- `POST /api/documents/{id}/reject`: Reject document with reasons.

## 4. Deficiencies (`/api/deficiencies`)
- `GET /api/deficiencies/application/{id}`: List deficiencies for an application.
- `POST /api/deficiencies`: Issue deficiency notice.
- `POST /api/deficiencies/{id}/response`: Applicant response submission.
- `POST /api/deficiencies/{id}/review`: Officer accept/reject response.

## 5. EVC Management (`/api/evc`)
- `POST /api/evc/visits`: Schedule EVC inspection visit.
- `POST /api/evc/visits/{id}/members`: Assign committee members (with conflict checks).
- `POST /api/evc/visits/{id}/conduct`: Record visit completion.
- `POST /api/evc/visits/{id}/report`: Upload inspection observation report.

## 6. Audit & Verification (`/api/audit`)
- `GET /api/audit`: Query tamper-evident audit logs with search filters.
- `GET /api/audit/verify-chain`: Verify SHA-256 hash-chain integrity.
- `GET /api/audit/export`: Export audit records to CSV.

## 7. AI Assistive Endpoints (`/api/ai`)
- `POST /api/ai/ocr/{documentId}`: OCR candidate field extraction.
- `POST /api/ai/classify`: Document type suggestion.
- `GET /api/ai/missing-documents/{applicationId}`: Checklist gap cross-check.
- `GET /api/ai/summary/{applicationId}`: Application executive brief.
- `GET /api/ai/search?query=...`: Safe parameterized natural language query translation.
- `POST /api/ai/confirm/{aiAssistanceId}`: Human officer sign-off and audit seal.
