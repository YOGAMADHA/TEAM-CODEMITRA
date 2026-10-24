# AICTE Approval Workflow Management System — Backend Service

Spring Boot 3.3 / Java 21 REST API backend powering the AICTE Approval Workflow Management System.

## Architecture & Responsibilities
- **Authentication & JWT Security**: Stateless token lifecycle, refresh token rotation, BCrypt password hashing.
- **Workflow State Machine**: 18-stage regulatory process tracking from `DRAFT` to `COMPETENT_AUTHORITY_DECISION`.
- **Cryptographic Audit Trail**: SHA-256 hash-chain ledger guaranteeing tamper-evidence across all administrative actions.
- **Document Management**: Dual storage support (MinIO S3 / Local Disk), SHA-256 deduplication, versioning, and MIME validation.
- **Deficiencies & EVC**: Structured defect notes, applicant response processing, EVC member scheduling conflict prevention.
- **AI Assistive Layer**: Pluggable OCR extraction and document classification with mandatory human-in-the-loop sign-off.
- **Observability**: Spring Boot Actuator health and metrics endpoints at `/actuator/health`.

## Requirements
- Java 21 JDK
- Maven 3.9+ (or use `./mvnw`)
- PostgreSQL 16
- MailHog (SMTP on port 1025)

## Running Locally

```bash
# Build and verify test suites
./mvnw clean verify

# Run Spring Boot service
./mvnw spring-boot:run
```

The service will start on port `8080`.
- OpenAPI / Swagger Documentation: `http://localhost:8080/swagger-ui.html`
- Health Endpoint: `http://localhost:8080/actuator/health`
