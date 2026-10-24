# AICTE Approval Workflow Management System — Frontend Client

React 18 / TypeScript single-page application built with Vite and Material UI (MUI v5).

## Features
- **Role-Based Portals**: Tailored interfaces for Applicants, Scrutiny Officers, Regional Officers, EVC Members, Committee Members, and Auditors.
- **Workflow Detail & Timeline**: Live stage tracking across all 18 regulatory lifecycle phases.
- **Auditor Verification Panel**: Real-time cryptographic SHA-256 hash-chain inspection, filtering, and CSV ledger export.
- **AI-Assisted Panels**: OCR data extraction and document checklist cross-checking with mandatory statutory disclaimers.
- **Real-Time Polling**: 30-second background polling for unread notifications and dashboard statistics.
- **Interactive Forms**: Zod-validated application creation wizard and deficiency response upload workflows.

## Prerequisites
- Node.js 20+
- npm 10+

## Getting Started

```bash
# Install dependencies
npm ci || npm install

# Run local development server
npm run dev

# Production build
npm run build
```

Development server runs on: `http://localhost:5173`
API requests are proxied or dispatched to: `http://localhost:8080/api`
