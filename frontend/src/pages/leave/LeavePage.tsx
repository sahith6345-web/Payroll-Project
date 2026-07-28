import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, LeaveRequest, Holiday } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Input } from '../../components/ui/Input';
import { Select } from '../../components/ui/Select';
import { Modal } from '../../components/ui/Modal';
import { CalendarDays, Plus, Check, X, Palmtree, HeartPulse, Sparkles } from 'lucide-react';

export const LeavePage: React.FC = () => {
  const [leaves, setLeaves] = useState<LeaveRequest[]>([]);
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isApplyModalOpen, setIsApplyModalOpen] = useState(false);

  // Form State
  const [leaveType, setLeaveType] = useState('CASUAL');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [reason, setReason] = useState('');

  useEffect(() => {
    fetchLeaves();
    fetchHolidays();
  }, []);

  const fetchLeaves = async () => {
    try {
      const res = await api.get<ApiResponse<LeaveRequest[]>>('/leaves/all');
      if (res.data.success) {
        setLeaves(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchHolidays = async () => {
    try {
      const res = await api.get<ApiResponse<Holiday[]>>('/leaves/holidays');
      if (res.data.success) {
        setHolidays(res.data.data);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleApplyLeave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/leaves/apply', {
        leaveType,
        startDate,
        endDate,
        reason,
      });
      setIsApplyModalOpen(false);
      fetchLeaves();
    } catch (err) {
      console.error(err);
    }
  };

  const handleStatusUpdate = async (id: string, status: 'APPROVED' | 'REJECTED') => {
    try {
      await api.put(`/leaves/${id}/status`, { status, comments: `Updated to ${status}` });
      fetchLeaves();
    } catch (err) {
      console.error(err);
    }
  };

  const columns = [
    { header: 'Type', accessor: (l: LeaveRequest) => <span className="font-semibold">{l.leaveType}</span> },
    { header: 'Start Date', accessor: 'startDate' as keyof LeaveRequest },
    { header: 'End Date', accessor: 'endDate' as keyof LeaveRequest },
    { header: 'Days', accessor: (l: LeaveRequest) => <span className="font-bold">{l.numberOfDays} days</span> },
    { header: 'Reason', accessor: 'reason' as keyof LeaveRequest },
    {
      header: 'Status',
      accessor: (l: LeaveRequest) => (
        <Badge
          variant={
            l.status === 'APPROVED' ? 'success' : l.status === 'REJECTED' ? 'danger' : 'warning'
          }
        >
          {l.status}
        </Badge>
      ),
    },
    {
      header: 'Actions',
      accessor: (l: LeaveRequest) =>
        l.status === 'PENDING' ? (
          <div className="flex items-center gap-2">
            <Button size="sm" variant="success" onClick={() => handleStatusUpdate(l.id, 'APPROVED')}>
              <Check className="w-3.5 h-3.5" />
            </Button>
            <Button size="sm" variant="danger" onClick={() => handleStatusUpdate(l.id, 'REJECTED')}>
              <X className="w-3.5 h-3.5" />
            </Button>
          </div>
        ) : (
          <span className="text-xs text-slate-400">Processed</span>
        ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
            Leave & Holiday Management
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Apply for paid leave, view balances, process approval workflows, and check public holidays.
          </p>
        </div>
        <Button onClick={() => setIsApplyModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
          Apply for Leave
        </Button>
      </div>

      {/* Leave Balances Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <Card hoverEffect className="flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Casual Leave Balance</p>
            <h3 className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-1">12 / 14 Days</h3>
            <p className="text-xs text-emerald-500 font-medium mt-1">2 days used this year</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-amber-500/10 text-amber-500 flex items-center justify-center">
            <Palmtree className="w-6 h-6" />
          </div>
        </Card>

        <Card hoverEffect className="flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Sick Leave Balance</p>
            <h3 className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-1">9 / 10 Days</h3>
            <p className="text-xs text-emerald-500 font-medium mt-1">1 day used this year</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-rose-500/10 text-rose-500 flex items-center justify-center">
            <HeartPulse className="w-6 h-6" />
          </div>
        </Card>

        <Card hoverEffect className="flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold text-slate-400 uppercase">Earned / Privilege Leave</p>
            <h3 className="text-2xl font-black text-slate-900 dark:text-slate-100 mt-1">15 Days</h3>
            <p className="text-xs text-indigo-500 font-medium mt-1">Encashable at year end</p>
          </div>
          <div className="w-12 h-12 rounded-xl bg-indigo-500/10 text-indigo-500 flex items-center justify-center">
            <Sparkles className="w-6 h-6" />
          </div>
        </Card>
      </div>

      {/* Leave Requests Table */}
      <div>
        <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100 mb-3">Leave Applications</h3>
        <Table columns={columns} data={leaves} keyExtractor={(l) => l.id} isLoading={isLoading} />
      </div>

      {/* Apply Leave Modal */}
      <Modal isOpen={isApplyModalOpen} onClose={() => setIsApplyModalOpen(false)} title="Apply for Time Off">
        <form onSubmit={handleApplyLeave} className="space-y-4">
          <Select
            label="Leave Type"
            value={leaveType}
            onChange={(e) => setLeaveType(e.target.value)}
            options={[
              { value: 'CASUAL', label: 'Casual Leave' },
              { value: 'SICK', label: 'Sick Leave' },
              { value: 'EARNED', label: 'Earned / Paid Leave' },
              { value: 'UNPAID', label: 'Unpaid Leave' },
            ]}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Start Date"
              type="date"
              required
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
            />
            <Input
              label="End Date"
              type="date"
              required
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
            />
          </div>
          <Input
            label="Reason for Absence"
            required
            placeholder="Brief explanation..."
            value={reason}
            onChange={(e) => setReason(e.target.value)}
          />
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsApplyModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">Submit Request</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
