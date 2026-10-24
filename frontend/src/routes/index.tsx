import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ProtectedRoute } from '../components/common/ProtectedRoute';
import { MainLayout } from '../components/layout/MainLayout';

// Pages
import { LoginPage } from '../pages/auth/LoginPage';
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { ApplicationListPage } from '../pages/applications/ApplicationListPage';
import { ApplicationCreatePage } from '../pages/applications/ApplicationCreatePage';
import { ApplicationDetailPage } from '../pages/applications/ApplicationDetailPage';
import { DeficiencyListPage } from '../pages/deficiencies/DeficiencyListPage';
import { EvcManagementPage } from '../pages/evc/EvcManagementPage';
import { ComplianceListPage } from '../pages/compliance/ComplianceListPage';
import { EscalationDashboard } from '../pages/escalations/EscalationDashboard';
import { NotificationPage } from '../pages/notifications/NotificationPage';
import { ReportDashboard } from '../pages/reports/ReportDashboard';
import { UserManagementPage } from '../pages/admin/UserManagementPage';
import { AuditLogPage } from '../pages/admin/AuditLogPage';
import { DemoModePage } from '../pages/demo/DemoModePage';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Public Auth Route */}
      <Route path="/login" element={<LoginPage />} />

      {/* Protected Routes */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/applications" element={<ApplicationListPage />} />
          <Route path="/applications/new" element={<ApplicationCreatePage />} />
          <Route path="/applications/:id" element={<ApplicationDetailPage />} />
          <Route path="/deficiencies" element={<DeficiencyListPage />} />
          <Route path="/evc" element={<EvcManagementPage />} />
          <Route path="/compliance" element={<ComplianceListPage />} />
          <Route path="/escalations" element={<EscalationDashboard />} />
          <Route path="/notifications" element={<NotificationPage />} />
          <Route path="/reports" element={<ReportDashboard />} />
          <Route path="/admin/users" element={<UserManagementPage />} />
          <Route path="/admin/audit" element={<AuditLogPage />} />
          <Route path="/demo-mode" element={<DemoModePage />} />
        </Route>
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
