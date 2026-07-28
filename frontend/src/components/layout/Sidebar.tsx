import React from 'react';
import { NavLink } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';
import {
  LayoutDashboard,
  Users,
  Building2,
  Clock,
  CalendarDays,
  DollarSign,
  Receipt,
  FileSpreadsheet,
  Settings,
  ShieldCheck
} from 'lucide-react';
import { clsx } from 'clsx';

export const Sidebar: React.FC = () => {
  const { user } = useSelector((state: RootState) => state.auth);
  const roles = user?.roles || [];

  const navItems = [
    { label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { label: 'Employees', path: '/employees', icon: Users },
    { label: 'Departments', path: '/departments', icon: Building2 },
    { label: 'Attendance', path: '/attendance', icon: Clock },
    { label: 'Leave & Holidays', path: '/leave', icon: CalendarDays },
    { label: 'Payroll Engine', path: '/payroll', icon: DollarSign },
    { label: 'Reimbursements', path: '/reimbursement', icon: Receipt },
    { label: 'Reports', path: '/reports', icon: FileSpreadsheet },
    { label: 'Settings', path: '/settings', icon: Settings },
  ];

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col flex-shrink-0 text-slate-300">
      {/* Brand Logo */}
      <div className="h-16 px-6 flex items-center gap-3 border-b border-slate-800">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 to-indigo-400 flex items-center justify-center text-white font-black text-xl shadow-lg shadow-indigo-500/30">
          P
        </div>
        <div>
          <h1 className="font-extrabold text-white text-base tracking-tight leading-none">PAYROLL</h1>
          <span className="text-[10px] text-indigo-400 font-semibold tracking-wider uppercase">Enterprise Edition</span>
        </div>
      </div>

      {/* Nav Menu */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <div className="px-3 text-[11px] font-semibold text-slate-500 uppercase tracking-wider mb-2">
          Main Navigation
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                clsx(
                  'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200',
                  isActive
                    ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20 font-semibold'
                    : 'text-slate-400 hover:bg-slate-800 hover:text-slate-100'
                )
              }
            >
              <Icon className="w-4 h-4" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Branding Info */}
      <div className="p-4 border-t border-slate-800 text-xs text-slate-500 text-center">
        Enterprise HRMS &bull; v1.0.0
      </div>
    </aside>
  );
};
