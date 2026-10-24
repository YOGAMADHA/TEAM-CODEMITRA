import api from './axios';
import { ApiResponse, DocumentChecklistDto, DocumentDto, VerifyDocumentRequest } from '../types';

export const documentsApi = {
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<DocumentDto[]>>(`/documents/application/${applicationId}`);
    return res.data;
  },
  getChecklist: async (applicationId: string) => {
    const res = await api.get<ApiResponse<DocumentChecklistDto[]>>(`/documents/application/${applicationId}/checklist`);
    return res.data;
  },
  upload: async (formData: FormData) => {
    const res = await api.post<ApiResponse<DocumentDto>>('/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
  download: (documentId: string) => {
    window.open(`/api/documents/${documentId}/download`, '_blank');
  },
  verify: async (documentId: string, data: VerifyDocumentRequest) => {
    const res = await api.post<ApiResponse<DocumentDto>>(`/documents/${documentId}/verify`, data);
    return res.data;
  },
  delete: async (documentId: string) => {
    const res = await api.delete<ApiResponse<void>>(`/documents/${documentId}`);
    return res.data;
  },
};
