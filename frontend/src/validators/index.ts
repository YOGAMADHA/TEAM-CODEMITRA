import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(1, 'Email or username is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

export const applicationCreateSchema = z.object({
  category: z.string().min(1, 'Application category is required'),
  academicYear: z.string().regex(/^\d{4}-\d{2}$/, 'Academic year must be in YYYY-YY format (e.g. 2024-25)'),
  programName: z.string().min(3, 'Program name must be at least 3 characters'),
  intakeRequested: z.number().int().positive('Intake must be a positive integer'),
  remarks: z.string().optional(),
});

export const deficiencyCreateSchema = z.object({
  category: z.string().min(1, 'Deficiency category is required'),
  description: z.string().min(10, 'Description must be at least 10 characters'),
  correctiveAction: z.string().min(10, 'Required corrective action is mandatory'),
  dueDate: z.string().min(1, 'Due date is required'),
});

export type LoginFormData = z.infer<typeof loginSchema>;
export type ApplicationCreateFormData = z.infer<typeof applicationCreateSchema>;
export type DeficiencyCreateFormData = z.infer<typeof deficiencyCreateSchema>;
