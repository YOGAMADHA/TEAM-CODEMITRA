import api from './axios';
import { ApiResponse, AuthResponse, LoginRequest, UserDto } from '../types';

export const authApi = {
  login: async (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', data);
    return res.data;
  },
  logout: async (refreshToken?: string): Promise<void> => {
    await api.post('/auth/logout', { refreshToken });
  },
  getMe: async (): Promise<ApiResponse<UserDto>> => {
    const res = await api.get<ApiResponse<UserDto>>('/auth/me');
    return res.data;
  },
  changePassword: async (data: any): Promise<ApiResponse<void>> => {
    const res = await api.post<ApiResponse<void>>('/auth/change-password', data);
    return res.data;
  },
};
