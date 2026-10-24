import api from './axios';
import { ApiResponse, NotificationDto } from '../types';

export const notificationsApi = {
  getUnread: async () => {
    const res = await api.get<ApiResponse<NotificationDto[]>>('/notifications');
    return res.data;
  },
  getAll: async () => {
    const res = await api.get<ApiResponse<NotificationDto[]>>('/notifications/all');
    return res.data;
  },
  getCount: async () => {
    const res = await api.get<ApiResponse<number>>('/notifications/count');
    return res.data;
  },
  markRead: async (id: string) => {
    const res = await api.post<ApiResponse<void>>(`/notifications/${id}/read`);
    return res.data;
  },
  markAllRead: async () => {
    const res = await api.post<ApiResponse<void>>('/notifications/read-all');
    return res.data;
  },
};
