import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, Department } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { Modal } from '../../components/ui/Modal';
import { Building2, Plus, Users, ShieldCheck } from 'lucide-react';

export const DepartmentPage: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const res = await api.get<ApiResponse<Department[]>>('/departments');
      if (res.data.success) {
        setDepartments(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/departments', { code, name, description, active: true });
      setIsModalOpen(false);
      setCode('');
      setName('');
      setDescription('');
      fetchDepartments();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
            Departments & Designations
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Organize company business units, organizational hierarchy, and team structures.
          </p>
        </div>
        <Button onClick={() => setIsModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
          Add Department
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {departments.map((dept) => (
          <Card key={dept.id} hoverEffect className="flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="font-mono text-xs font-bold text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-950/60 px-2.5 py-1 rounded-md border border-indigo-200 dark:border-indigo-800">
                  {dept.code}
                </span>
                <Badge variant={dept.active ? 'success' : 'neutral'}>
                  {dept.active ? 'Active' : 'Inactive'}
                </Badge>
              </div>
              <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">{dept.name}</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-2 line-clamp-2">
                {dept.description || 'Core business department.'}
              </p>
            </div>
            <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-xs text-slate-500">
              <div className="flex items-center gap-1.5">
                <Users className="w-4 h-4 text-indigo-500" />
                <span>Active Headcount</span>
              </div>
              <span className="font-bold text-slate-900 dark:text-slate-100">8 Members</span>
            </div>
          </Card>
        ))}
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Create New Department">
        <form onSubmit={handleCreate} className="space-y-4">
          <Input
            label="Department Code"
            placeholder="e.g. ENG, FIN, MKT"
            required
            value={code}
            onChange={(e) => setCode(e.target.value)}
          />
          <Input
            label="Department Name"
            placeholder="e.g. Software Engineering"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <Input
            label="Description"
            placeholder="Brief overview of responsibilities..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">Create Department</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
