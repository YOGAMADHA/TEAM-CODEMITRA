import api from './axios';
import { ApiResponse, ApplicationDto, ApplicationStageHistoryDto, CreateApplicationRequest, StageTransitionRequest } from '../types';

export const applicationsApi = {
  getAll: async (page = 0, size = 10) => {
    const res = await api.get<ApiResponse<{ content: ApplicationDto[]; totalElements: number }>>(
      `/applications?page=${page}&size=${size}`
    );
    return res.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<ApplicationDto>>(`/applications/${id}`);
    return res.data;
  },
  getAssigned: async (page = 0, size = 10) => {
    const res = await api.get<ApiResponse<{ content: ApplicationDto[]; totalElements: number }>>(
      `/applications/assigned?page=${page}&size=${size}`
    );
    return res.data;
  },
  create: async (data: CreateApplicationRequest) => {
    const res = await api.post<ApiResponse<ApplicationDto>>('/applications', data);
    return res.data;
  },
  submit: async (id: string) => {
    const res = await api.post<ApiResponse<ApplicationDto>>(`/applications/${id}/submit`);
    return res.data;
  },
  transition: async (id: string, data: StageTransitionRequest) => {
    const res = await api.post<ApiResponse<ApplicationDto>>(`/applications/${id}/transition`, data);
    return res.data;
  },
  assign: async (id: string, officerId: string, remarks?: string) => {
    const res = await api.post<ApiResponse<ApplicationDto>>(`/applications/${id}/assign`, { officerId, remarks });
    return res.data;
  },
  withdraw: async (id: string) => {
    const res = await api.post<ApiResponse<ApplicationDto>>(`/applications/${id}/withdraw`);
    return res.data;
  },
  getTimeline: async (id: string) => {
    const res = await api.get<ApiResponse<ApplicationStageHistoryDto[]>>(`/applications/${id}/timeline`);
    return res.data;
  },
};
