import api from './axios';
import { ApiResponse, CreateDeadlineRequest, DeadlineDto } from '../types';

export const deadlinesApi = {
  getByApplication: async (applicationId: string) => {
    const res = await api.get<ApiResponse<DeadlineDto[]>>(`/deadlines/application/${applicationId}`);
    return res.data;
  },
  create: async (data: CreateDeadlineRequest) => {
    const res = await api.post<ApiResponse<DeadlineDto>>('/deadlines', data);
    return res.data;
  },
  getOverdue: async () => {
    const res = await api.get<ApiResponse<DeadlineDto[]>>('/deadlines/overdue');
    return res.data;
  },
  resolve: async (id: string) => {
    const res = await api.post<ApiResponse<DeadlineDto>>(`/deadlines/${id}/resolve`);
    return res.data;
  },
};
