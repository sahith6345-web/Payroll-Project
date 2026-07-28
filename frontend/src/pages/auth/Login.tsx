import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { setCredentials } from '../../store/authSlice';
import api from '../../services/api';
import { ApiResponse, AuthResponse } from '../../types';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import { Lock, Mail, Shield, Sparkles } from 'lucide-react';

export const Login: React.FC = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [usernameOrEmail, setUsernameOrEmail] = useState('superadmin@payroll.com');
  const [password, setPassword] = useState('Admin@12345');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', {
        usernameOrEmail,
        password,
      });

      if (res.data.success) {
        dispatch(
          setCredentials({
            user: res.data.data.user,
            accessToken: res.data.data.accessToken,
            refreshToken: res.data.data.refreshToken,
          })
        );
        navigate('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid username or password credentials');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Dynamic Ambient Glow background */}
      <div className="absolute -top-40 -left-40 w-96 h-96 bg-indigo-600/30 rounded-full blur-3xl" />
      <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-purple-600/20 rounded-full blur-3xl" />

      <div className="w-full max-w-md bg-slate-900/80 backdrop-blur-xl border border-slate-800 p-8 rounded-2xl shadow-2xl relative z-10">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-indigo-600/20 text-indigo-400 border border-indigo-500/30 mb-3 shadow-inner">
            <Shield className="w-7 h-7" />
          </div>
          <h2 className="text-2xl font-extrabold text-white tracking-tight">Enterprise Payroll Portal</h2>
          <p className="text-xs text-slate-400 mt-1">Sign in with your organizational credentials</p>
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs text-center font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <Input
            label="Username or Email"
            type="text"
            placeholder="superadmin@payroll.com"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
            leftIcon={<Mail className="w-4 h-4" />}
            required
          />

          <Input
            label="Password"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            leftIcon={<Lock className="w-4 h-4" />}
            required
          />

          <Button type="submit" className="w-full mt-2" isLoading={isLoading} size="lg">
            Sign In to Dashboard
          </Button>
        </form>

        {/* Preset quick test accounts */}
        <div className="mt-8 pt-6 border-t border-slate-800">
          <div className="text-xs font-semibold text-slate-400 mb-2 flex items-center justify-center gap-1">
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            <span>Quick Login Credentials:</span>
          </div>
          <div className="grid grid-cols-2 gap-2 text-[11px]">
            <button
              type="button"
              onClick={() => { setUsernameOrEmail('superadmin@payroll.com'); setPassword('Admin@12345'); }}
              className="p-2 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-left transition-colors border border-slate-700/60"
            >
              <div className="font-bold text-indigo-400">Super Admin</div>
              <div className="truncate">superadmin@payroll.com</div>
            </button>
            <button
              type="button"
              onClick={() => { setUsernameOrEmail('hr@payroll.com'); setPassword('Hr@12345'); }}
              className="p-2 rounded bg-slate-800 hover:bg-slate-700 text-slate-300 text-left transition-colors border border-slate-700/60"
            >
              <div className="font-bold text-emerald-400">HR Manager</div>
              <div className="truncate">hr@payroll.com</div>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
