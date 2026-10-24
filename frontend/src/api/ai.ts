import api from './axios';
import { ApiResponse, AiClassificationResult, AiOcrResult, AiSummaryResult } from '../types';

export const aiApi = {
  ocrDocument: async (documentId: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post<ApiResponse<AiOcrResult>>(`/ai/ocr/${documentId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  classifyDocument: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post<ApiResponse<AiClassificationResult>>('/ai/classify', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  checkMissingDocuments: async (applicationId: string) => {
    const res = await api.get<ApiResponse<{
      disclaimer: string;
      modelName: string;
      taskType: string;
      applicationId: string;
      possiblyMissing: { requirementName: string; reason: string }[];
      requiresHumanConfirmation: boolean;
    }>>(`/ai/missing-documents/${applicationId}`);
    return res.data;
  },

  getApplicationSummary: async (applicationId: string) => {
    const res = await api.get<ApiResponse<AiSummaryResult>>(`/ai/summary/${applicationId}`);
    return res.data;
  },

  searchNaturalLanguage: async (query: string) => {
    const res = await api.get<ApiResponse<{
      disclaimer: string;
      modelName: string;
      taskType: string;
      originalQuery: string;
      structuredFilters: Record<string, unknown>;
      requiresHumanConfirmation: boolean;
    }>>(`/ai/search?query=${encodeURIComponent(query)}`);
    return res.data;
  },

  confirmAiOutput: async (aiAssistanceId: string, remarks?: string) => {
    const res = await api.post<ApiResponse<string>>(
      `/ai/confirm/${aiAssistanceId}${remarks ? `?remarks=${encodeURIComponent(remarks)}` : ''}`
    );
    return res.data;
  },
};
