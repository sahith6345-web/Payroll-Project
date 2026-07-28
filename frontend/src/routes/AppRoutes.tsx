import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Login } from '../pages/auth/Login';
import { DashboardLayout } from '../components/layout/DashboardLayout';
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { EmployeeListPage } from '../pages/employee/EmployeeListPage';
import { DepartmentPage } from '../pages/department/DepartmentPage';
import { AttendancePage } from '../pages/attendance/AttendancePage';
import { LeavePage } from '../pages/leave/LeavePage';
import { PayrollPage } from '../pages/payroll/PayrollPage';
import { ReimbursementPage } from '../pages/reimbursement/ReimbursementPage';
import { ReportsPage } from '../pages/reports/ReportsPage';
import { SettingsPage } from '../pages/settings/SettingsPage';
import { ProtectedRoute } from './ProtectedRoute';

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      {/* Protected Routes inside Dashboard Layout */}
      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/employees" element={<EmployeeListPage />} />
          <Route path="/departments" element={<DepartmentPage />} />
          <Route path="/attendance" element={<AttendancePage />} />
          <Route path="/leave" element={<LeavePage />} />
          <Route path="/payroll" element={<PayrollPage />} />
          <Route path="/reimbursement" element={<ReimbursementPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};
