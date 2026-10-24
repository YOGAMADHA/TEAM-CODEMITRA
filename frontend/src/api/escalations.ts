import api from './axios';
import { ApiResponse, CreateEscalationRequest, EscalationDto, ResolveEscalationRequest } from '../types';

export const escalationsApi = {
  getActive: async () => {
    const res = await api.get<ApiResponse<EscalationDto[]>>('/escalations');
    return res.data;
  },
  getMy: async () => {
    const res = await api.get<ApiResponse<EscalationDto[]>>('/escalations/my');
    return res.data;
  },
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<EscalationDto[]>>(`/escalations/application/${applicationId}`);
    return res.data;
  },
  create: async (data: CreateEscalationRequest) => {
    const res = await api.post<ApiResponse<EscalationDto>>('/escalations', data);
    return res.data;
  },
  resolve: async (id: string, data: ResolveEscalationRequest) => {
    const res = await api.post<ApiResponse<EscalationDto>>(`/escalations/${id}/resolve`, data);
    return res.data;
  },
};
