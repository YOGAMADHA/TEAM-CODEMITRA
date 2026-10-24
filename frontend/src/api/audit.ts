import api from './axios';
import { ApiResponse, AuditLog, HashChainVerificationResult } from '../types';

export const auditApi = {
  getAll: async (page = 0, size = 15, filters?: { entityType?: string; action?: string; performedBy?: string; from?: string; to?: string }) => {
    let url = `/audit?page=${page}&size=${size}`;
    if (filters?.entityType) url += `&entityType=${encodeURIComponent(filters.entityType)}`;
    if (filters?.action) url += `&action=${encodeURIComponent(filters.action)}`;
    if (filters?.performedBy) url += `&performedBy=${encodeURIComponent(filters.performedBy)}`;
    if (filters?.from) url += `&from=${encodeURIComponent(filters.from)}`;
    if (filters?.to) url += `&to=${encodeURIComponent(filters.to)}`;

    const res = await api.get<ApiResponse<{ content: AuditLog[]; totalElements: number; totalPages: number }>>(url);
    return res.data;
  },

  getByEntity: async (entityType: string, entityId: string | number) => {
    const res = await api.get<ApiResponse<AuditLog[]>>(`/audit/entity/${entityType}/${entityId}`);
    return res.data;
  },

  verifyChain: async () => {
    const res = await api.get<ApiResponse<HashChainVerificationResult>>('/audit/verify-chain');
    return res.data;
  },

  exportLogs: async (filters?: { entityType?: string; action?: string; performedBy?: string; from?: string; to?: string }) => {
    let url = `/audit/export?`;
    const params = new URLSearchParams();
    if (filters?.entityType) params.append('entityType', filters.entityType);
    if (filters?.action) params.append('action', filters.action);
    if (filters?.performedBy) params.append('performedBy', filters.performedBy);
    if (filters?.from) params.append('from', filters.from);
    if (filters?.to) params.append('to', filters.to);

    const res = await api.get<ApiResponse<AuditLog[]>>(`${url}${params.toString()}`);
    return res.data;
  },
};
