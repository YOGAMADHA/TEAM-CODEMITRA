# AICTE Approval Workflow Management System

> **Mandatory Transparency Notice & Academic Prototype Disclaimer:**  
> This is an academic prototype workflow-management platform. It does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

---

## 1. Project Title
**AICTE Approval Workflow Management System** (Prototype Reference Model)  
Target Repository: [https://github.com/YOGAMADHA/TEAM-CODEMITRA](https://github.com/YOGAMADHA/TEAM-CODEMITRA)

## 2. Project Purpose
This platform provides an auditable, transparent, multi-stakeholder workflow management system modeled after the approval process for technical institutions in India. It manages application lifecycles, document verification, defect handling, inspection scheduling, deadline tracking, and administrative decision recording.

## 3. Human-Supervision Limitation
Automated engines handle deadline reminders, checklist comparisons, and notifications. However, **all statutory approvals, rejections, document authentications, and compliance certifications must be executed by authorized human officers**. Algorithms and AI tools serve solely in an assistive capacity.

## 4. Features
- **18-Stage Regulatory Workflow**: Complete lifecycle tracking from `DRAFT` to `COMPETENT_AUTHORITY_DECISION`.
- **Cryptographic Hash-Chain Audit Trail**: Immutable transaction ledger with SHA-256 chaining and a one-click chain verification tool.
- **Document Management**: Dual-mode storage (MinIO S3 / local filesystem), SHA-256 duplicate detection, version incrementing, and human verification.
- **Deficiency Handling**: Structured defect notes, corrective action requirements, applicant response uploads, and officer re-scrutiny.
- **Expert Visit Committee (EVC)**: Inspection scheduling, panel member roster conflict detection, and observation report verification.
- **Statutory Deadlines & Escalation Engine**: Automated reminders (`-30, -15, -7, 0, 3, 7, 15` days) and background escalations.
- **AI-Assisted Features with Governance**: OCR metadata extraction, document classification suggestions, missing document cross-checks, and parameterized natural language search.
- **Security Hardening**: Bucket4j login rate limiting (5 attempts/min/IP), Spring Security JWT authentication, multi-tenant institutional data isolation, and secure HTTP headers.
- **Analytics & SLA Dashboards**: Pendency metrics, overdue SLA tracking, stage distribution visualizers, and CSV export.

## 5. Technology Stack
- **Backend**: Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA, Hibernate, Bucket4j, Flyway, Spring Boot Actuator, OpenAPI / Swagger UI.
- **Frontend**: React 18, TypeScript, Vite, Material UI (MUI v5), React Router v6, Axios, Zod.
- **Database & Storage**: PostgreSQL 16, MinIO Object Storage, MailHog (SMTP testing).
- **DevOps**: Docker, Docker Compose, Nginx, GitHub Actions CI.

## 6. System Architecture
Clean multi-tier architecture separating the client SPA, security filters, Spring Boot business services, PostgreSQL database, and MinIO object storage. Full details in [docs/architecture.md](docs/architecture.md).

## 7. Repository Structure
```
TEAM-CODEMITRA/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       ├── frontend-ci.yml
│       └── security-scan.yml
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── README.md
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src/
├── frontend/
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   ├── Dockerfile
│   ├── index.html
│   ├── README.md
│   └── src/
├── database/
│   ├── seed/
│   ├── sample-data/
│   └── README.md
├── docs/
│   ├── architecture.md
│   ├── database-design.md
│   ├── er-diagram.md
│   ├── api-documentation.md
│   ├── workflow-documentation.md
│   ├── process-matrix.md
│   ├── role-permission-matrix.md
│   ├── testing-plan.md
│   ├── deployment-guide.md
│   ├── ai-usage-and-limitations.md
│   ├── security-notes.md
│   ├── user-manual.md
│   └── project-presentation-content.md
├── nginx/
│   └── nginx.conf
├── scripts/
│   ├── start-dev.sh
│   ├── stop-dev.sh
│   ├── reset-database.sh
│   ├── run-tests.sh
│   └── verify-project.sh
├── .env.example
├── .gitignore
├── .dockerignore
├── docker-compose.yml
├── LICENSE
├── README.md
└── CHANGELOG.md
```

## 8. Prerequisites
- Docker Desktop 24+ & Docker Compose v2
- Java 21 JDK & Maven 3.9+ (optional if using Docker)
- Node.js 20+ & npm 10+ (optional if using Docker)

## 9. Environment Setup
Copy the template configuration:
```bash
cp .env.example .env
```
Configure database credentials, JWT secret, and storage bucket settings in `.env`.

## 10. Docker Setup
Launch the complete containerized stack:
```bash
docker compose up -d --build
```
Access endpoints:
- Frontend Client: `http://localhost:3000`
- Backend REST API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health Endpoint: `http://localhost:8080/actuator/health`
- MailHog Web Dashboard: `http://localhost:8025`
- MinIO Storage Console: `http://localhost:9001`

## 11. Local Development Setup
Run infrastructure containers only:
```bash
./scripts/start-dev.sh
```
Run Backend:
```bash
cd backend
./mvnw spring-boot:run
```
Run Frontend:
```bash
cd frontend
npm ci || npm install
npm run dev
```

## 12. Database Setup
PostgreSQL database migrations are managed automatically on startup via Flyway (`V1` through `V7`). To manually reset or re-seed the database:
```bash
./scripts/reset-database.sh
```

## 13. Demo User Accounts
Standard demonstration password: `Password@123`

| Username | Role | Primary Function in Demo |
|---|---|---|
| `admin` | `ADMIN` | System administrator, user provisioning, audit inspection |
| `scrutiny_officer` | `SCRUTINY_OFFICER` | Document verification, deficiency issuance, AI sign-off |
| `evc_coordinator` | `EVC_COORDINATOR` | Inspection committee panel scheduling, report receipt |
| `auditor` | `AUDITOR` | Read-only inspection, SHA-256 hash-chain verification tool |
| `abc_applicant` | `APPLICANT` | ABC Engineering College application (Scrutiny stage) |
| `xyz_applicant` | `APPLICANT` | XYZ Polytechnic application (Deficiency open stage) |
| `pqr_applicant` | `APPLICANT` | PQR Management Institute application (EVC report pending) |

## 14. API Documentation Link
When backend is running, access Swagger OpenAPI UI at:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 15. Test Commands
Execute backend tests:
```bash
cd backend
./mvnw clean test
```
Or execute the automated test runner:
```bash
./scripts/run-tests.sh
```

## 16. Deployment Instructions
Refer to [docs/deployment-guide.md](docs/deployment-guide.md) for production deployment topologies, multi-container configurations, and database backup/restore procedures.

## 17. GitHub Workflow Instructions
Automated CI pipelines in `.github/workflows/`:
- `backend-ci.yml`: Compiles Java 21 backend and executes unit and integration tests.
- `frontend-ci.yml`: Performs npm install, type check, and production build.
- `security-scan.yml`: Scans for secret leaks, checks dependencies, and audits packages.

## 18. Known Limitations
- Virus scanning defaults to `NoOpVirusScanService` (placeholder); production deployments must connect to a live ClamAV daemon.
- MinIO integration uses local disk abstraction as default; S3 endpoint credentials can be enabled via configuration.

## 19. AI Limitations
Refer to [docs/ai-usage-and-limitations.md](docs/ai-usage-and-limitations.md). AI outputs are strictly advisory. AI cannot make approvals, rejections, or legal interpretations.

## 20. License and Contribution Notes
Distributed under the Apache License 2.0. See [LICENSE](LICENSE) for terms. Developed for academic evaluation and demonstration.
