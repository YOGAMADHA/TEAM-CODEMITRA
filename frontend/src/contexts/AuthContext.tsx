import React, { createContext, useContext, useState, useEffect } from 'react';
import { UserDto } from '../types';
import { authApi } from '../api/auth';

interface AuthContextType {
  user: UserDto | null;
  isAuthenticated: boolean;
  login: (token: string, refreshToken: string, user: UserDto, permissions: string[]) => void;
  logout: () => void;
  hasPermission: (permission: string) => boolean;
  hasRole: (role: string) => boolean;
  permissions: string[];
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserDto | null>(() => {
    const saved = localStorage.getItem('user');
    return saved ? JSON.parse(saved) : null;
  });
  const [permissions, setPermissions] = useState<string[]>(() => {
    const saved = localStorage.getItem('permissions');
    return saved ? JSON.parse(saved) : [];
  });

  const login = (token: string, refreshToken: string, userData: UserDto, userPermissions: string[]) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('permissions', JSON.stringify(userPermissions));
    setUser(userData);
    setPermissions(userPermissions);
  };

  const logout = () => {
    const rf = localStorage.getItem('refreshToken');
    if (rf) authApi.logout(rf).catch(() => {});
    localStorage.clear();
    setUser(null);
    setPermissions([]);
    window.location.href = '/login';
  };

  const hasPermission = (permission: string) => permissions.includes(permission);
  const hasRole = (role: string) => (user?.roles ? user.roles.includes(role) : false);

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        login,
        logout,
        hasPermission,
        hasRole,
        permissions,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
