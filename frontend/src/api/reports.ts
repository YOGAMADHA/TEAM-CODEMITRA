import api from './axios';
import { ApiResponse, ApplicationDto, ApplicationSummaryDto, PendencyReportDto, PerformanceReportDto } from '../types';

export const reportsApi = {
  getPendency: async () => {
    const res = await api.get<ApiResponse<PendencyReportDto>>('/reports/pendency');
    return res.data;
  },
  getPerformance: async (officerId: string) => {
    const res = await api.get<ApiResponse<PerformanceReportDto>>(`/reports/performance/${officerId}`);
    return res.data;
  },
  getSummary: async () => {
    const res = await api.get<ApiResponse<ApplicationSummaryDto>>('/reports/summary');
    return res.data;
  },
  getOverdue: async () => {
    const res = await api.get<ApiResponse<ApplicationDto[]>>('/reports/overdue');
    return res.data;
  },
};
