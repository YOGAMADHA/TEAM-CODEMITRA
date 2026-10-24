import api from './axios';
import { ApiResponse, CreateUserRequest, UpdateUserRequest, UserDto } from '../types';

export const usersApi = {
  getAll: async (page = 0, size = 10) => {
    const res = await api.get<ApiResponse<{ content: UserDto[]; totalElements: number }>>(
      `/users?page=${page}&size=${size}`
    );
    return res.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<UserDto>>(`/users/${id}`);
    return res.data;
  },
  create: async (data: CreateUserRequest) => {
    const res = await api.post<ApiResponse<UserDto>>('/users', data);
    return res.data;
  },
  update: async (id: string, data: UpdateUserRequest) => {
    const res = await api.put<ApiResponse<UserDto>>(`/users/${id}`, data);
    return res.data;
  },
  activate: async (id: string) => {
    const res = await api.post<ApiResponse<void>>(`/users/${id}/activate`);
    return res.data;
  },
  deactivate: async (id: string) => {
    const res = await api.post<ApiResponse<void>>(`/users/${id}/deactivate`);
    return res.data;
  },
};
