# Entity-Relationship (ER) Diagram

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

```mermaid
erDiagram
    INSTITUTION ||--o{ USER : employs
    INSTITUTION ||--o{ APPLICATION : submits
    USER ||--o{ REFRESH_TOKEN : owns
    ROLE ||--o{ USER : assigned_to
    ROLE ||--o{ ROLE_PERMISSION : contains
    PERMISSION ||--o{ ROLE_PERMISSION : mapped_in

    APPLICATION ||--o{ APPLICATION_STAGE_HISTORY : tracks
    APPLICATION ||--o{ DOCUMENT : contains
    APPLICATION ||--o{ DEFICIENCY : has
    APPLICATION ||--o{ EVC_VISIT : schedules
    APPLICATION ||--o{ DEADLINE : bound_by
    APPLICATION ||--o{ NOTIFICATION : triggers
    APPLICATION ||--o{ AI_ASSISTANCE : analyzed_by

    DOCUMENT_REQUIREMENT ||--o{ DOCUMENT : satisfies
    DEFICIENCY ||--o{ DEFICIENCY_RESPONSE : receives
    EVC_VISIT ||--o{ EVC_MEMBER : includes

    AUDIT_LOG {
        UUID id PK
        string entity_type
        UUID entity_id
        UUID user_id
        string action
        string performed_by
        string previous_hash
        string current_hash
        timestamp performed_at
    }

    APPLICATION {
        UUID id PK
        string application_number
        UUID institution_id FK
        string category
        string current_stage
        string status
        int intake_requested
        int version
    }

    DOCUMENT {
        UUID id PK
        UUID application_id FK
        UUID requirement_id FK
        string file_name
        string storage_key
        string checksum
        int version_number
        string verification_status
    }
```
