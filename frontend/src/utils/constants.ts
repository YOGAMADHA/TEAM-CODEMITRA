import type { ApplicationStage, DeficiencyStatus, DocumentVerificationStatus, EvcStatus } from '@/types';

export const APPLICATION_STAGES: ApplicationStage[] = [
  'DRAFT',
  'SUBMITTED',
  'INITIAL_VALIDATION',
  'SCRUTINY',
  'DEFICIENCY_RAISED',
  'APPLICANT_RESPONSE',
  'RESCRUTINY',
  'EVC_REQUIRED',
  'EVC_SCHEDULING',
  'EVC_CONDUCTED',
  'EVC_REPORT_PENDING',
  'EVC_REPORT_UPLOADED',
  'COMMITTEE_REVIEW',
  'COMPETENT_AUTHORITY_DECISION',
  'APPROVED',
  'REJECTED',
  'RETURNED',
  'WITHDRAWN',
];

export const DEFICIENCY_STATUSES: DeficiencyStatus[] = [
  'OPEN',
  'RESPONSE_SUBMITTED',
  'UNDER_REVIEW',
  'RESOLVED',
  'REJECTED',
  'OVERDUE',
  'REOPENED',
];

export const DOCUMENT_VERIFICATION_STATUSES: DocumentVerificationStatus[] = [
  'NOT_REVIEWED',
  'UNDER_REVIEW',
  'VERIFIED',
  'REJECTED',
  'EXPIRED',
  'REPLACEMENT_REQUIRED',
];

export const EVC_STATUSES: EvcStatus[] = [
  'EVC_REQUIRED',
  'MEMBERS_ASSIGNED',
  'DATE_PROPOSED',
  'DATE_CONFIRMED',
  'VISIT_REMINDER_SENT',
  'VISIT_COMPLETED',
  'REPORT_PENDING',
  'REPORT_UPLOADED',
  'REPORT_VERIFIED',
  'RESCHEDULED',
  'CANCELLED',
];

export const ROLES = {
  ADMIN: 'ROLE_ADMIN',
  OFFICER: 'ROLE_OFFICER',
  APPLICANT: 'ROLE_APPLICANT',
  EVC_MEMBER: 'ROLE_EVC_MEMBER',
  COMMITTEE_MEMBER: 'ROLE_COMMITTEE_MEMBER',
} as const;

export const DRAWER_WIDTH = 260;

export const APPLICATION_CATEGORIES = [
  'New Institution',
  'Extension of Approval',
  'Change of Site',
  'Change of Name',
  'Closure',
  'Addition of Course',
  'Increase in Intake',
  'Supernumerary Quota',
];

export const INSTITUTION_TYPES = [
  'University',
  'Deemed University',
  'Private University',
  'Government',
  'Government Aided',
  'Private (Self-Financing)',
  'Public-Private Partnership',
];

export const MEETING_MODES = ['PHYSICAL', 'VIRTUAL', 'HYBRID'] as const;

export const PRIORITY_OPTIONS = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'] as const;
