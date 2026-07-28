import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, Reimbursement } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Input } from '../../components/ui/Input';
import { Select } from '../../components/ui/Select';
import { Modal } from '../../components/ui/Modal';
import { Receipt, Plus, Check, X, DollarSign } from 'lucide-react';

export const ReimbursementPage: React.FC = () => {
  const [claims, setClaims] = useState<Reimbursement[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitModalOpen, setIsSubmitModalOpen] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [expenseType, setExpenseType] = useState('TRAVEL');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    fetchClaims();
  }, []);

  const fetchClaims = async () => {
    try {
      const res = await api.get<ApiResponse<Reimbursement[]>>('/reimbursements/all');
      if (res.data.success) {
        setClaims(res.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmitClaim = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/reimbursements/submit', {
        title,
        expenseType,
        amount: parseFloat(amount),
        description,
      });
      setIsSubmitModalOpen(false);
      setTitle('');
      setAmount('');
      setDescription('');
      fetchClaims();
    } catch (err) {
      console.error(err);
    }
  };

  const handleStatusUpdate = async (id: string, status: 'APPROVED' | 'REJECTED') => {
    try {
      await api.put(`/reimbursements/${id}/status`, { status, comments: `Claim ${status}` });
      fetchClaims();
    } catch (err) {
      console.error(err);
    }
  };

  const columns = [
    { header: 'Title', accessor: 'title' as keyof Reimbursement },
    { header: 'Category', accessor: 'expenseType' as keyof Reimbursement },
    {
      header: 'Amount',
      accessor: (r: Reimbursement) => (
        <span className="font-bold text-slate-900 dark:text-slate-100">
          ${(r.amount || 0).toLocaleString()}
        </span>
      ),
    },
    { header: 'Date', accessor: 'expenseDate' as keyof Reimbursement },
    {
      header: 'Status',
      accessor: (r: Reimbursement) => (
        <Badge
          variant={
            r.status === 'APPROVED' ? 'success' : r.status === 'REJECTED' ? 'danger' : 'warning'
          }
        >
          {r.status}
        </Badge>
      ),
    },
    {
      header: 'Actions',
      accessor: (r: Reimbursement) =>
        r.status === 'PENDING' ? (
          <div className="flex items-center gap-2">
            <Button size="sm" variant="success" onClick={() => handleStatusUpdate(r.id, 'APPROVED')}>
              <Check className="w-3.5 h-3.5" />
            </Button>
            <Button size="sm" variant="danger" onClick={() => handleStatusUpdate(r.id, 'REJECTED')}>
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
            Expense Reimbursements
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Submit business expense claims, upload receipts, and manage manager approvals.
          </p>
        </div>
        <Button onClick={() => setIsSubmitModalOpen(true)} leftIcon={<Plus className="w-4 h-4" />}>
          Submit New Claim
        </Button>
      </div>

      <Table columns={columns} data={claims} keyExtractor={(r) => r.id} isLoading={isLoading} />

      <Modal isOpen={isSubmitModalOpen} onClose={() => setIsSubmitModalOpen(false)} title="Submit Expense Claim">
        <form onSubmit={handleSubmitClaim} className="space-y-4">
          <Input
            label="Claim Title"
            required
            placeholder="e.g. Client Dinner, Travel Flight"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Expense Category"
              value={expenseType}
              onChange={(e) => setExpenseType(e.target.value)}
              options={[
                { value: 'TRAVEL', label: 'Travel & Transport' },
                { value: 'MEALS', label: 'Client Meals & Entertainment' },
                { value: 'EQUIPMENT', label: 'Hardware / Office Equipment' },
                { value: 'OTHER', label: 'Other Business Expense' },
              ]}
            />
            <Input
              label="Amount ($)"
              type="number"
              required
              placeholder="150.00"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
            />
          </div>
          <Input
            label="Description & Business Purpose"
            placeholder="Details of expense..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsSubmitModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">Submit Claim</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
