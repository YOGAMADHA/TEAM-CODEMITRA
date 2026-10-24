# Deployment Guide

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. Local Containerized Deployment

To launch the full suite (PostgreSQL 16, MinIO, MailHog, Spring Boot backend, and React frontend):

```bash
docker compose up -d --build
```

### Deployed Services
- **Frontend SPA**: `http://localhost:3000`
- **Backend REST API**: `http://localhost:8080`
- **OpenAPI / Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Actuator Health Check**: `http://localhost:8080/actuator/health`
- **MailHog SMTP Web UI**: `http://localhost:8025`
- **MinIO Storage Console**: `http://localhost:9001`

## 2. Local Manual Setup

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Node.js 20+
- Docker (for PostgreSQL, MinIO, MailHog)

### Step 1: Start Infrastructure
```bash
docker compose up -d postgres minio mailhog
```

### Step 2: Run Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Step 3: Run Frontend
```bash
cd frontend
npm ci || npm install
npm run dev
```
Development client runs on `http://localhost:5173`.

## 3. Database Backup & Disaster Recovery

### Create Backup
```bash
docker exec -t aicte-postgres pg_dump -U aicte_user -d aicte_db > backup_aicte_$(date +%Y%m%d_%H%M%S).sql
```

### Restore Backup
```bash
docker exec -i aicte-postgres psql -U aicte_user -d aicte_db < backup_file.sql
```
