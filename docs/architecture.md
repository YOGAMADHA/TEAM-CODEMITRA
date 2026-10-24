# Architecture Documentation

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. System Topology

The AICTE Approval Workflow Management System is built on a clean multi-tier modular architecture:

```
[ Client Tier: React 18 + MUI v5 + Vite ]
                   │
                   ▼  HTTPS / REST / JWT Bearer
[ Security Tier: Rate Limiting (Bucket4j) + Spring Security 6 ]
                   │
                   ▼
[ Application Tier: Spring Boot 3.3 Services ]
  ├── WorkflowEngine & State Machine
  ├── DocumentManagement & MinIO Storage
  ├── Deficiency & Response Engine
  ├── EVC Scheduling & Coordination
  ├── Deadline & Escalation Background Schedulers
  ├── Audit & Cryptographic Hash-Chain Ledger
  └── AI-Assisted Advisory Service (Human-in-the-Loop)
                   │
       ┌───────────┴───────────┐
       ▼                       ▼
[ Relational DB Tier ]   [ Storage Tier ]
PostgreSQL 16 (Flyway)    MinIO Object Storage / Local Filesystem
```

## 2. Key Architectural Invariants
1. **Administrative Authority:** Automated routines manage deadlines, checklists, and notifications, but **all approvals, rejections, and verifications must be executed by authenticated human officers**.
2. **Cryptographic Sealing:** Every state transition and modification generates a SHA-256 hash chaining record:
   $$\text{currentHash} = \text{SHA-256}(\text{previousHash} \parallel \text{entityType} \parallel \text{entityId} \parallel \text{userId} \parallel \text{action} \parallel \text{performedBy} \parallel \text{performedAt})$$
3. **Institutional Data Isolation:** Strict object-level access boundaries ensure applicants can only view their own institution's data.
4. **Resilient Local Storage:** Decoupled storage abstraction (`StorageService`) supporting MinIO S3 and local filesystem (`./uploads`).
