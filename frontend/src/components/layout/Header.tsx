import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '../../store';
import { toggleTheme } from '../../store/themeSlice';
import { logout } from '../../store/authSlice';
import { Sun, Moon, LogOut, User as UserIcon, Bell } from 'lucide-react';
import { Badge } from '../ui/Badge';

export const Header: React.FC = () => {
  const dispatch = useDispatch();
  const { user } = useSelector((state: RootState) => state.auth);
  const { isDark } = useSelector((state: RootState) => state.theme);

  const primaryRole = user?.roles?.[0] || 'EMPLOYEE';

  return (
    <header className="sticky top-0 z-30 h-16 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 px-6 flex items-center justify-between transition-colors duration-200">
      <div className="flex items-center gap-3">
        <h2 className="text-base font-bold text-slate-800 dark:text-slate-100 hidden sm:block">
          Enterprise Payroll Portal
        </h2>
      </div>

      <div className="flex items-center gap-4">
        {/* Dark Mode Toggle */}
        <button
          onClick={() => dispatch(toggleTheme())}
          className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 dark:text-slate-400 transition-colors"
          title="Toggle Theme"
        >
          {isDark ? <Sun className="w-5 h-5 text-amber-400" /> : <Moon className="w-5 h-5 text-indigo-600" />}
        </button>

        {/* Notifications Icon */}
        <button className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 dark:text-slate-400 relative transition-colors">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-indigo-600 rounded-full" />
        </button>

        <div className="h-6 w-px bg-slate-200 dark:bg-slate-800" />

        {/* User Info Dropdown / Pill */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-indigo-600/10 dark:bg-indigo-500/20 text-indigo-600 dark:text-indigo-400 font-bold flex items-center justify-center border border-indigo-500/20">
            {user?.firstName?.charAt(0) || 'U'}
          </div>
          <div className="hidden md:block text-left">
            <div className="text-sm font-semibold text-slate-800 dark:text-slate-100 leading-none mb-1">
              {user?.firstName} {user?.lastName}
            </div>
            <Badge variant="info" size="sm">
              {primaryRole}
            </Badge>
          </div>

          <button
            onClick={() => dispatch(logout())}
            className="p-2 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/40 transition-colors"
            title="Logout"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>
    </header>
  );
};
