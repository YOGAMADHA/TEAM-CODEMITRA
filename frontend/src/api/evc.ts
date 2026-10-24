import api from './axios';
import { ApiResponse, AssignEvcMemberRequest, CreateEvcVisitRequest, EvcMemberDto, EvcVisitDto, ProposeDateRequest, RescheduleEvcRequest, VerifyEvcReportRequest } from '../types';

export const evcApi = {
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<EvcVisitDto[]>>(`/evc/application/${applicationId}`);
    return res.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<EvcVisitDto>>(`/evc/${id}`);
    return res.data;
  },
  create: async (data: CreateEvcVisitRequest) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>('/evc', data);
    return res.data;
  },
  assignMember: async (id: string, data: AssignEvcMemberRequest) => {
    const res = await api.post<ApiResponse<EvcMemberDto>>(`/evc/${id}/assign-member`, data);
    return res.data;
  },
  proposeDate: async (id: string, data: ProposeDateRequest) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/propose-date`, data);
    return res.data;
  },
  confirmDate: async (id: string) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/confirm-date`);
    return res.data;
  },
  reschedule: async (id: string, data: RescheduleEvcRequest) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/reschedule`, data);
    return res.data;
  },
  complete: async (id: string) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/complete`);
    return res.data;
  },
  uploadReport: async (id: string, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/upload-report`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },
  verifyReport: async (id: string, data: VerifyEvcReportRequest) => {
    const res = await api.post<ApiResponse<EvcVisitDto>>(`/evc/${id}/verify-report`, data);
    return res.data;
  },
};
