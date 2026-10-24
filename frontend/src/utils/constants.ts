import type { ApplicationStage, DeficiencyStatus, DocumentVerificationStatus, EvcStatus } from '@/types';

export const APPLICATION_STAGES: ApplicationStage[] = [
  'DRAFT',
  'SUBMITTED',
  'SCRUTINY',
  'DOCUMENT_VERIFICATION',
  'DEFICIENCY_CHECK',
  'EVC_SCHEDULED',
  'EVC_COMPLETED',
  'COMMITTEE_REVIEW',
  'STANDING_COMMITTEE',
  'EXECUTIVE_COMMITTEE',
  'COUNCIL_MEETING',
  'APPROVED',
  'REJECTED',
  'WITHDRAWN',
];

export const DEFICIENCY_STATUSES: DeficiencyStatus[] = [
  'OPEN',
  'RESPONDED',
  'REVIEWED',
  'RESOLVED',
  'REOPENED',
  'OVERDUE',
];

export const DOCUMENT_VERIFICATION_STATUSES: DocumentVerificationStatus[] = [
  'PENDING',
  'VERIFIED',
  'REJECTED',
  'RE_UPLOAD_REQUIRED',
];

export const EVC_STATUSES: EvcStatus[] = [
  'PROPOSED',
  'DATE_PROPOSED',
  'DATE_CONFIRMED',
  'SCHEDULED',
  'IN_PROGRESS',
  'COMPLETED',
  'REPORT_SUBMITTED',
  'REPORT_VERIFIED',
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
