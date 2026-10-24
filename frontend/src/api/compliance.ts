import api from './axios';
import { ApiResponse, ComplianceItemDto, CreateComplianceItemRequest } from '../types';

export const complianceApi = {
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<ComplianceItemDto[]>>(`/compliance/application/${applicationId}`);
    return res.data;
  },
  create: async (data: CreateComplianceItemRequest) => {
    const res = await api.post<ApiResponse<ComplianceItemDto>>('/compliance', data);
    return res.data;
  },
  fulfill: async (id: string, documentId?: string) => {
    const url = documentId ? `/compliance/${id}/fulfill?documentId=${documentId}` : `/compliance/${id}/fulfill`;
    const res = await api.post<ApiResponse<ComplianceItemDto>>(url);
    return res.data;
  },
  getExpiring: async () => {
    const res = await api.get<ApiResponse<ComplianceItemDto[]>>('/compliance/expiring');
    return res.data;
  },
};
