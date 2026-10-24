import api from './axios';
import { ApiResponse, CreateInstitutionRequest, InstitutionDto } from '../types';

export const institutionsApi = {
  getAll: async (page = 0, size = 10) => {
    const res = await api.get<ApiResponse<{ content: InstitutionDto[]; totalElements: number }>>(
      `/institutions?page=${page}&size=${size}`
    );
    return res.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<InstitutionDto>>(`/institutions/${id}`);
    return res.data;
  },
  create: async (data: CreateInstitutionRequest) => {
    const res = await api.post<ApiResponse<InstitutionDto>>('/institutions', data);
    return res.data;
  },
  update: async (id: string, data: CreateInstitutionRequest) => {
    const res = await api.put<ApiResponse<InstitutionDto>>(`/institutions/${id}`, data);
    return res.data;
  },
};
