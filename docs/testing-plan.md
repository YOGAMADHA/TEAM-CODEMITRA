# Testing Plan & Scenario Verification

> **Important Notice:** This is a prototype workflow-management system for academic demonstration. It supports human-supervised administrative processing and does not replace official AICTE systems, statutory scrutiny, inspection, committee review, or competent-authority decisions.

## Test Matrix Overview

| Test Identifier | Scope | Test Class / Method | Expected Pass Criteria |
|---|---|---|---|
| `TEST-01` | SHA-256 Hash Determinism | `AuditHashChainTest.sha256ShouldBeDeterministic` | Hash is consistent and 64 hex characters |
| `TEST-02` | Cryptographic Chain Verification | `AuditHashChainTest.shouldVerifyIntactChain` | Chain integrity passes across consecutive blocks |
| `TEST-03` | Genesis Block Anchor | `AuditHashChainTest.firstLogShouldUseGenesisPreviousHash` | First record anchors to `previousHash = "GENESIS"` |
| `TEST-04` | Audit Log Immutability | `AuditHashChainTest.auditLogMustBeImmutable` | Primary key and audit fields are non-updatable |
| `TEST-05` | Document Duplicate Detection | `DocumentServiceTest.checksumShouldDetectDuplicates` | Matching SHA-256 identifies duplicates |
| `TEST-06` | Allowed File Types Whitelist | `DocumentServiceTest.shouldValidateAllowedExtensions` | Accepts PDF, JPG, PNG, DOCX; rejects others |
| `TEST-07` | Maximum File Size Enforcement | `DocumentServiceTest.shouldRejectOversizedFiles` | Files > 10MB are rejected |
| `TEST-08` | Version Increment on Re-upload | `DocumentServiceTest.documentVersionShouldIncrement` | Re-uploading increments `versionNumber` |
| `TEST-09` | Soft Deletion Audit Retention | `DocumentServiceTest.softDeletionShouldNotRemoveDocument` | Soft delete retains binary metadata in audit |
| `TEST-10` | Valid Stage Transitions | `WorkflowServiceTest.validTransitionsShouldBePermitted` | Valid state transitions succeed |
| `TEST-11` | Invalid Stage Jump Prevention | `WorkflowServiceTest.invalidTransitionsShouldBeRejected` | Arbitrary jumps across stages fail validation |
| `TEST-12` | Terminal State Invariance | `WorkflowServiceTest.withdrawnStageShouldBeTerminal` | `APPROVED` and `WITHDRAWN` states cannot transition |
| `TEST-13` | Institutional Tenant Isolation | `SecurityAccessTest.applicantShouldNotAccessOtherInstitution` | Cross-institution access returns 403 Forbidden |
| `TEST-14` | Auditor Read-Only Enforcement | `SecurityAccessTest.auditorShouldHaveReadOnlyAccess` | Auditor cannot call mutation endpoints |
| `TEST-15` | Rate Limiter Protection | `SecurityAccessTest.rateLimitShouldBlockAfter5Attempts` | Exceeding 5 login calls/min returns 429 |
| `TEST-16` | BCrypt Password Hashing | `SecurityAccessTest.passwordMustBeBcryptHashed` | Hashes have length >= 60 and start with `$2a$` |
| `TEST-17` | AI Mandatory Disclaimer Banner | `MockAiServiceTest.ocrExtractionMustIncludeDisclaimer` | AI outputs always include statutory warning |
| `TEST-18` | AI Zero-Statutory Decision | `MockAiServiceTest.aiMustNeverReturnDecision` | AI declines statutory decision recommendation |

## How to Execute Tests
```bash
cd backend
./mvnw clean test
```
