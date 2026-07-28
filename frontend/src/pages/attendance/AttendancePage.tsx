import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, Attendance } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Clock, LogIn, LogOut, CheckCircle2, Calendar } from 'lucide-react';

export const AttendancePage: React.FC = () => {
  const [attendances, setAttendances] = useState<Attendance[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [clockInTime, setClockInTime] = useState<string | null>(null);
  const [clockOutTime, setClockOutTime] = useState<string | null>(null);
  const [isClocking, setIsClocking] = useState(false);

  useEffect(() => {
    fetchAttendance();
  }, []);

  const fetchAttendance = async () => {
    try {
      const res = await api.get<ApiResponse<Attendance[]>>('/attendance/all');
      if (res.data.success) {
        setAttendances(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleClockIn = async () => {
    setIsClocking(true);
    try {
      const res = await api.post<ApiResponse<Attendance>>('/attendance/clock-in');
      if (res.data.success) {
        setClockInTime(res.data.data.checkIn || new Date().toLocaleTimeString());
        fetchAttendance();
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsClocking(false);
    }
  };

  const handleClockOut = async () => {
    setIsClocking(true);
    try {
      const res = await api.post<ApiResponse<Attendance>>('/attendance/clock-out');
      if (res.data.success) {
        setClockOutTime(res.data.data.checkOut || new Date().toLocaleTimeString());
        fetchAttendance();
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsClocking(false);
    }
  };

  const columns = [
    { header: 'Date', accessor: 'date' as keyof Attendance },
    {
      header: 'Check In',
      accessor: (att: Attendance) => (
        <span className="font-mono text-xs font-semibold text-emerald-600 dark:text-emerald-400">
          {att.checkIn || '--:--'}
        </span>
      ),
    },
    {
      header: 'Check Out',
      accessor: (att: Attendance) => (
        <span className="font-mono text-xs font-semibold text-rose-600 dark:text-rose-400">
          {att.checkOut || '--:--'}
        </span>
      ),
    },
    {
      header: 'Work Hours',
      accessor: (att: Attendance) => (
        <span className="font-bold text-slate-800 dark:text-slate-200">
          {att.workHours ? `${att.workHours.toFixed(1)} hrs` : '0 hrs'}
        </span>
      ),
    },
    {
      header: 'Status',
      accessor: (att: Attendance) => (
        <Badge variant={att.status === 'PRESENT' ? 'success' : 'danger'}>{att.status}</Badge>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
            Attendance & Shifts
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Track daily clock in/out timestamps, working hours, and shift compliance.
          </p>
        </div>
      </div>

      {/* Clock In / Out Action Panel */}
      <Card className="bg-gradient-to-r from-indigo-900 to-slate-900 border-indigo-800 text-white p-6">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-indigo-600/30 border border-indigo-400/30 flex items-center justify-center text-indigo-300">
              <Clock className="w-8 h-8 animate-pulse" />
            </div>
            <div>
              <div className="text-xs uppercase tracking-wider text-indigo-300 font-bold">Today's Shift Status</div>
              <h3 className="text-xl font-black mt-0.5">Regular Morning Shift (09:00 - 18:00)</h3>
              <p className="text-xs text-slate-300 mt-1">
                {clockInTime ? `Clocked in at ${clockInTime}` : 'Not clocked in yet today.'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 w-full md:w-auto">
            <Button
              onClick={handleClockIn}
              isLoading={isClocking}
              variant="success"
              size="lg"
              className="flex-1 md:flex-none"
              leftIcon={<LogIn className="w-4 h-4" />}
            >
              Clock In
            </Button>
            <Button
              onClick={handleClockOut}
              isLoading={isClocking}
              variant="danger"
              size="lg"
              className="flex-1 md:flex-none"
              leftIcon={<LogOut className="w-4 h-4" />}
            >
              Clock Out
            </Button>
          </div>
        </div>
      </Card>

      {/* Attendance History */}
      <div>
        <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100 mb-3">Attendance History</h3>
        <Table columns={columns} data={attendances} keyExtractor={(item) => item.id} isLoading={isLoading} />
      </div>
    </div>
  );
};
