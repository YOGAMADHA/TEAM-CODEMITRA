import api from './axios';
import { ApiResponse, CreateDeficiencyRequest, CreateDeficiencyResponseRequest, DeficiencyDto, DeficiencyResponseDto, ReviewDeficiencyRequest } from '../types';

export const deficienciesApi = {
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<DeficiencyDto[]>>(`/deficiencies/application/${applicationId}`);
    return res.data;
  },
  create: async (data: CreateDeficiencyRequest) => {
    const res = await api.post<ApiResponse<DeficiencyDto>>('/deficiencies', data);
    return res.data;
  },
  respond: async (id: string, data: CreateDeficiencyResponseRequest) => {
    const res = await api.post<ApiResponse<DeficiencyResponseDto>>(`/deficiencies/${id}/respond`, data);
    return res.data;
  },
  review: async (responseId: string, data: ReviewDeficiencyRequest) => {
    const res = await api.post<ApiResponse<DeficiencyResponseDto>>(`/deficiencies/response/${responseId}/review`, data);
    return res.data;
  },
  reopen: async (id: string, reason: string) => {
    const res = await api.post<ApiResponse<DeficiencyDto>>(`/deficiencies/${id}/reopen?reason=${encodeURIComponent(reason)}`);
    return res.data;
  },
};
