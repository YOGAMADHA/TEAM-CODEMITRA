# Security Architecture & Hardening Notes

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## 1. Authentication & Rate Limiting
- **JWT Lifecyle**: Access tokens expire in 24 hours; refresh tokens are rotated and stored in PostgreSQL.
- **BCrypt Password Hashing**: Passwords stored using BCrypt with strength factor 12.
- **Login Rate Limiting**: `RateLimitingFilter` applies Bucket4j token-bucket rate limiting (max 5 attempts per minute per IP address on `/api/auth/login`).

## 2. Institutional Isolation (Multi-Tenancy)
- Applicants are bound to their institution ID. Attempts to access or mutate other institutions return `403 Forbidden`.
- Global queries for officers verify assigned permissions via `@PreAuthorize`.

## 3. Auditor Read-Only Enforcement
- The `AUDITOR` role has strictly read-only capabilities across all inspection and report endpoints.

## 4. Secure HTTP Headers
- `Strict-Transport-Security`: `max-age=31536000; includeSubDomains`
- `X-Frame-Options`: `DENY`
- `X-Content-Type-Options`: `nosniff`
- `Referrer-Policy`: `strict-origin-when-cross-origin`

## 5. Storage Security & Virus Scan Abstraction
- All uploaded files pass through `VirusScanService` before persisting.
- File extensions are restricted to an allowed whitelist (`pdf`, `jpg`, `jpeg`, `png`, `docx`).
- File size is capped at 10MB by default.
