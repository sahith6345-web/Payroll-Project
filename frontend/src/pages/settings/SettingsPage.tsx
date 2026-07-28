import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, AuditLog } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Table } from '../../components/ui/Table';
import { Building, ShieldCheck, Lock, History } from 'lucide-react';

export const SettingsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'company' | 'security' | 'audit'>('company');
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [isLoadingLogs, setIsLoadingLogs] = useState(false);

  // Security password change state
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [passwordMsg, setPasswordMsg] = useState('');

  useEffect(() => {
    if (activeTab === 'audit') {
      fetchAuditLogs();
    }
  }, [activeTab]);

  const fetchAuditLogs = async () => {
    setIsLoadingLogs(true);
    try {
      const res = await api.get<ApiResponse<AuditLog[]>>('/audit-logs');
      if (res.data.success) {
        setLogs(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoadingLogs(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordMsg('');
    try {
      await api.post('/auth/change-password', { oldPassword, newPassword });
      setPasswordMsg('Password changed successfully!');
      setOldPassword('');
      setNewPassword('');
    } catch (err: any) {
      setPasswordMsg(err.response?.data?.message || 'Failed to change password');
    }
  };

  const auditColumns = [
    { header: 'Action', accessor: (l: AuditLog) => <span className="font-bold font-mono text-indigo-400">{l.action || 'USER_LOGIN'}</span> },
    { header: 'Performed By', accessor: (l: AuditLog) => <span>{l.performedBy || 'superadmin@payroll.com'}</span> },
    { header: 'Details', accessor: (l: AuditLog) => <span>{l.details || 'User session authenticated successfully'}</span> },
    { header: 'Timestamp', accessor: (l: AuditLog) => <span className="text-xs text-slate-400">{l.timestamp || new Date().toLocaleString()}</span> },
  ];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
          System Settings & Audit Logs
        </h1>
        <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
          Configure corporate entity profiles, security authentication, and system activity logs.
        </p>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 border-b border-slate-200 dark:border-slate-800 pb-3">
        <Button
          variant={activeTab === 'company' ? 'primary' : 'ghost'}
          onClick={() => setActiveTab('company')}
          leftIcon={<Building className="w-4 h-4" />}
        >
          Company Profile
        </Button>
        <Button
          variant={activeTab === 'security' ? 'primary' : 'ghost'}
          onClick={() => setActiveTab('security')}
          leftIcon={<Lock className="w-4 h-4" />}
        >
          Security & Password
        </Button>
        <Button
          variant={activeTab === 'audit' ? 'primary' : 'ghost'}
          onClick={() => setActiveTab('audit')}
          leftIcon={<History className="w-4 h-4" />}
        >
          Audit Activity Logs
        </Button>
      </div>

      {/* Company Profile Tab */}
      {activeTab === 'company' && (
        <Card className="max-w-2xl space-y-4 p-6">
          <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">Corporate Information</h3>
          <Input label="Company Name" defaultValue="Enterprise HRMS & Payroll Inc." />
          <Input label="Registered Address" defaultValue="100 Innovation Way, Tech Park, Suite 400" />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Support Email" defaultValue="hr@enterprise-payroll.com" />
            <Input label="Tax ID / EIN" defaultValue="EIN-99-8877665" />
          </div>
          <Button className="mt-2">Save Company Profile</Button>
        </Card>
      )}

      {/* Security Tab */}
      {activeTab === 'security' && (
        <Card className="max-w-md space-y-4 p-6">
          <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">Change Password</h3>
          {passwordMsg && (
            <p className="text-xs font-semibold text-indigo-500 bg-indigo-50 dark:bg-indigo-950 p-2 rounded">
              {passwordMsg}
            </p>
          )}
          <form onSubmit={handleChangePassword} className="space-y-4">
            <Input
              label="Current Password"
              type="password"
              required
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
            />
            <Input
              label="New Password"
              type="password"
              required
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <Button type="submit">Update Password</Button>
          </form>
        </Card>
      )}

      {/* Audit Logs Tab */}
      {activeTab === 'audit' && (
        <div>
          <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100 mb-3">Security Audit Trail</h3>
          <Table columns={auditColumns} data={logs} keyExtractor={(l) => l.id || Math.random().toString()} isLoading={isLoadingLogs} />
        </div>
      )}
    </div>
  );
};
