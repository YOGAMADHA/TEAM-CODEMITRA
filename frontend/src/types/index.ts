// ─── Enums (string unions) ───────────────────────────────────────────────────

export type ApplicationStage =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'INITIAL_VALIDATION'
  | 'SCRUTINY'
  | 'DEFICIENCY_RAISED'
  | 'APPLICANT_RESPONSE'
  | 'RESCRUTINY'
  | 'EVC_REQUIRED'
  | 'EVC_SCHEDULING'
  | 'EVC_CONDUCTED'
  | 'EVC_REPORT_PENDING'
  | 'EVC_REPORT_UPLOADED'
  | 'COMMITTEE_REVIEW'
  | 'COMPETENT_AUTHORITY_DECISION'
  | 'APPROVED'
  | 'REJECTED'
  | 'RETURNED'
  | 'WITHDRAWN';

export type ApplicationStatus =
  | 'ACTIVE'
  | 'ON_HOLD'
  | 'CLOSED'
  | 'WITHDRAWN';

export type DocumentVerificationStatus =
  | 'NOT_REVIEWED'
  | 'UNDER_REVIEW'
  | 'VERIFIED'
  | 'REJECTED'
  | 'EXPIRED'
  | 'REPLACEMENT_REQUIRED';

export type DeficiencyStatus =
  | 'OPEN'
  | 'RESPONSE_SUBMITTED'
  | 'UNDER_REVIEW'
  | 'RESOLVED'
  | 'REJECTED'
  | 'OVERDUE'
  | 'REOPENED';

export type EvcStatus =
  | 'EVC_REQUIRED'
  | 'MEMBERS_ASSIGNED'
  | 'DATE_PROPOSED'
  | 'DATE_CONFIRMED'
  | 'VISIT_REMINDER_SENT'
  | 'VISIT_COMPLETED'
  | 'REPORT_PENDING'
  | 'REPORT_UPLOADED'
  | 'REPORT_VERIFIED'
  | 'RESCHEDULED'
  | 'CANCELLED';

export type MeetingMode = 'IN_PERSON' | 'VIRTUAL' | 'HYBRID';

export type NotificationType =
  | 'APPLICATION_SUBMITTED'
  | 'STAGE_CHANGED'
  | 'DEFICIENCY_RAISED'
  | 'DEFICIENCY_RESPONSE'
  | 'DEADLINE_REMINDER'
  | 'ESCALATION_ALERT'
  | 'DOCUMENT_UPLOADED'
  | 'DOCUMENT_VERIFIED'
  | 'DOCUMENT_REJECTED'
  | 'EVC_SCHEDULED'
  | 'EVC_REPORT_DUE'
  | 'ASSIGNMENT_NOTIFICATION'
  | 'PASSWORD_RESET'
  | 'ACCOUNT_ACTIVATION';

export type EscalationLevel =
  | 'LEVEL_1'
  | 'LEVEL_2'
  | 'LEVEL_3'
  | 'CRITICAL';

export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

// ─── Core DTOs ───────────────────────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
  errors?: Record<string, string>;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ─── Auth ────────────────────────────────────────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

// ─── User, Role, Permission ─────────────────────────────────────────────────

export interface Permission {
  id: number;
  name: string;
  description: string;
  module: string;
}

export interface Role {
  id: number;
  name: string;
  description: string;
  permissions: Permission[];
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  designation?: string;
  department?: string;
  roles: Role[];
  active: boolean;
  institutionId?: number;
  institutionName?: string;
  createdAt: string;
  updatedAt: string;
  lastLogin?: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  designation?: string;
  department?: string;
  roleIds: number[];
  institutionId?: number;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
  designation?: string;
  department?: string;
  roleIds?: number[];
  active?: boolean;
}

// ─── Institution ─────────────────────────────────────────────────────────────

export interface Institution {
  id: number;
  name: string;
  code: string;
  type: string;
  category: string;
  address: string;
  city: string;
  state: string;
  pinCode: string;
  phone: string;
  email: string;
  website?: string;
  contactPersonName: string;
  contactPersonEmail: string;
  contactPersonPhone: string;
  establishedYear?: number;
  universityAffiliation?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateInstitutionRequest {
  name: string;
  code: string;
  type: string;
  category: string;
  address: string;
  city: string;
  state: string;
  pinCode: string;
  phone: string;
  email: string;
  website?: string;
  contactPersonName: string;
  contactPersonEmail: string;
  contactPersonPhone: string;
  establishedYear?: number;
  universityAffiliation?: string;
}

export interface UpdateInstitutionRequest extends Partial<CreateInstitutionRequest> {}

// ─── Application ─────────────────────────────────────────────────────────────

export interface Application {
  id: number;
  applicationNumber: string;
  institutionId: number;
  institutionName: string;
  category: string;
  academicYear: string;
  stage: ApplicationStage;
  status: ApplicationStatus;
  priority: Priority;
  assignedOfficerId?: number;
  assignedOfficerName?: string;
  applicantId: number;
  applicantName: string;
  remarks?: string;
  submittedAt?: string;
  currentStageEnteredAt?: string;
  currentStageDueDate?: string;
  completedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateApplicationRequest {
  institutionId: number;
  category: string;
  academicYear: string;
  remarks?: string;
}

export interface UpdateApplicationRequest {
  remarks?: string;
  priority?: Priority;
}

export interface TransitionRequest {
  toStage: ApplicationStage;
  remarks?: string;
}

export interface AssignRequest {
  officerId: number;
  remarks?: string;
}

export interface ApplicationStageHistory {
  id: number;
  applicationId: number;
  fromStage?: ApplicationStage;
  toStage: ApplicationStage;
  performedById: number;
  performedByName: string;
  remarks?: string;
  enteredAt: string;
  exitedAt?: string;
  durationHours?: number;
}

// ─── Document ────────────────────────────────────────────────────────────────

export interface Document {
  id: number;
  applicationId: number;
  requirementId?: number;
  requirementName?: string;
  fileName: string;
  originalFileName: string;
  fileSize: number;
  mimeType: string;
  documentType: string;
  verificationStatus: DocumentVerificationStatus;
  verifiedById?: number;
  verifiedByName?: string;
  verifiedAt?: string;
  verificationRemarks?: string;
  issueDate?: string;
  expiryDate?: string;
  uploadedById: number;
  uploadedByName: string;
  createdAt: string;
}

export interface DocumentRequirement {
  id: number;
  category: string;
  documentName: string;
  description: string;
  mandatory: boolean;
  maxFileSize: number;
  allowedFileTypes: string[];
  sortOrder: number;
}

export interface DocumentChecklist {
  requirement: DocumentRequirement;
  document?: Document;
  uploaded: boolean;
  verified: boolean;
}

export interface DocumentUploadRequest {
  applicationId: number;
  requirementId?: number;
  documentType: string;
  issueDate?: string;
  expiryDate?: string;
}

export interface DocumentVerifyRequest {
  status: DocumentVerificationStatus;
  remarks?: string;
}

// ─── Deficiency ──────────────────────────────────────────────────────────────

export interface Deficiency {
  id: number;
  applicationId: number;
  applicationNumber: string;
  category: string;
  description: string;
  correctiveAction: string;
  status: DeficiencyStatus;
  priority: Priority;
  dueDate: string;
  raisedById: number;
  raisedByName: string;
  responseText?: string;
  responseDocumentId?: number;
  respondedAt?: string;
  reviewRemarks?: string;
  reviewedById?: number;
  reviewedByName?: string;
  reviewedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDeficiencyRequest {
  applicationId: number;
  category: string;
  description: string;
  correctiveAction: string;
  priority: Priority;
  dueDate: string;
}

export interface DeficiencyResponse {
  responseText: string;
  documentId?: number;
}

export interface DeficiencyReviewRequest {
  status: DeficiencyStatus;
  reviewRemarks: string;
}

// ─── EVC ─────────────────────────────────────────────────────────────────────

export interface EvcMember {
  id: number;
  evcVisitId: number;
  userId: number;
  userName: string;
  role: string;
  designation: string;
  confirmed: boolean;
  confirmedAt?: string;
}

export interface EvcVisit {
  id: number;
  applicationId: number;
  applicationNumber: string;
  institutionName: string;
  status: EvcStatus;
  meetingMode: MeetingMode;
  proposedDate?: string;
  confirmedDate?: string;
  actualDate?: string;
  venue?: string;
  meetingLink?: string;
  agenda?: string;
  reportDocumentId?: number;
  reportVerified: boolean;
  reportVerifiedById?: number;
  reportVerifiedByName?: string;
  reportVerificationRemarks?: string;
  members: EvcMember[];
  createdById: number;
  createdByName: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEvcRequest {
  applicationId: number;
  meetingMode: MeetingMode;
  venue?: string;
  meetingLink?: string;
  agenda?: string;
}

export interface AssignEvcMemberRequest {
  userId: number;
  role: string;
  designation: string;
}

export interface ProposeDateRequest {
  proposedDate: string;
}

export interface ConfirmDateRequest {
  confirmedDate: string;
}

export interface RescheduleRequest {
  newDate: string;
  reason: string;
}

export interface VerifyReportRequest {
  verified: boolean;
  remarks: string;
}

// ─── Compliance ──────────────────────────────────────────────────────────────

export interface ComplianceItem {
  id: number;
  applicationId: number;
  description: string;
  category: string;
  mandatory: boolean;
  fulfilled: boolean;
  fulfilledAt?: string;
  fulfilledById?: number;
  fulfilledByName?: string;
  dueDate?: string;
  remarks?: string;
  createdAt: string;
}

export interface CreateComplianceRequest {
  applicationId: number;
  description: string;
  category: string;
  mandatory: boolean;
  dueDate?: string;
}

export interface FulfillComplianceRequest {
  remarks?: string;
}

// ─── Deadline ────────────────────────────────────────────────────────────────

export interface Deadline {
  id: number;
  applicationId: number;
  applicationNumber: string;
  stage: ApplicationStage;
  description: string;
  dueDate: string;
  overdue: boolean;
  completedAt?: string;
  createdAt: string;
}

// ─── Notification ────────────────────────────────────────────────────────────

export interface Notification {
  id: number;
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  applicationId?: number;
  applicationNumber?: string;
  read: boolean;
  readAt?: string;
  createdAt: string;
}

// ─── Escalation ──────────────────────────────────────────────────────────────

export interface Escalation {
  id: number;
  applicationId: number;
  applicationNumber: string;
  level: EscalationLevel;
  reason: string;
  assignedToId: number;
  assignedToName: string;
  resolved: boolean;
  resolvedAt?: string;
  resolvedById?: number;
  resolvedByName?: string;
  resolutionRemarks?: string;
  createdAt: string;
}

export interface ResolveEscalationRequest {
  resolutionRemarks: string;
}

// ─── Audit Log ───────────────────────────────────────────────────────────────

export interface AuditLog {
  id: string | number;
  entityType: string;
  entityId: string | number;
  userId?: string | number;
  action: string;
  performedBy?: string;
  performedById?: number;
  performedByName?: string;
  userAgent?: string;
  performedAt?: string;
  oldValue?: string;
  newValue?: string;
  ipAddress?: string;
  remarks?: string;
  previousHash?: string;
  currentHash?: string;
  timestamp?: string;
}

export interface HashChainVerificationResult {
  totalRecords: number;
  verifiedRecords: number;
  brokenLinks: string[];
  chainIntact: boolean;
  verifiedAt: string;
}

// ─── AI Assistance ───────────────────────────────────────────────────────────

export interface AiAssistance {
  id: string;
  documentId?: string;
  applicationId?: string;
  requestedBy: string;
  taskType: 'OCR_EXTRACTION' | 'DOCUMENT_CLASSIFICATION' | 'MISSING_DOCUMENT_CHECK' | 'APPLICATION_SUMMARY' | 'NL_SEARCH';
  modelName: string;
  prompt?: string;
  output: string;
  confidence?: number;
  requestedAt: string;
  humanConfirmed: boolean;
  humanConfirmationRemarks?: string;
  humanConfirmedAt?: string;
  humanConfirmedBy?: string;
  disclaimer?: string;
}

export interface AiOcrResult {
  disclaimer: string;
  modelName: string;
  taskType: string;
  confidence: number;
  data: {
    institutionName?: string;
    certificateNumber?: string;
    issueDate?: string;
    expiryDate?: string;
    address?: string;
  };
  requiresHumanConfirmation: boolean;
}

export interface AiClassificationResult {
  disclaimer: string;
  modelName: string;
  taskType: string;
  confidence: number;
  suggestedType: string;
  alternativeTypes: string[];
  reasoning: string;
  requiresHumanConfirmation: boolean;
}

export interface AiSummaryResult {
  disclaimer: string;
  modelName: string;
  taskType: string;
  applicationId: string;
  summary: {
    overview?: string;
    institutionDetails?: string;
    documentStatus?: string;
    openDeficiencies?: string;
    deadlineStatus?: string;
    evcStatus?: string;
    recommendation?: string;
  };
  requiresHumanConfirmation: boolean;
}

// ─── Reports ─────────────────────────────────────────────────────────────────

export interface PendencyReport {
  stage: ApplicationStage;
  count: number;
  averageDays: number;
  overdueCount: number;
}

export interface PerformanceReport {
  officerId: number;
  officerName: string;
  assignedCount: number;
  completedCount: number;
  averageProcessingDays: number;
  overdueCount: number;
}

export interface OverdueReport {
  applicationId: number;
  applicationNumber: string;
  institutionName: string;
  stage: ApplicationStage;
  dueDate: string;
  overdueDays: number;
  assignedOfficerName?: string;
}

export interface SummaryReport {
  totalApplications: number;
  activeApplications: number;
  approvedCount: number;
  rejectedCount: number;
  pendingDeficiencies: number;
  scheduledEvcCount: number;
  overdueCount: number;
  averageProcessingDays: number;
  applicationsByStage: Record<ApplicationStage, number>;
  applicationsByCategory: Record<string, number>;
}
