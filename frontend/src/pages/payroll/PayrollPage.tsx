import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, Payroll, PayrollItem } from '../../types';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Table } from '../../components/ui/Table';
import { Badge } from '../../components/ui/Badge';
import { Select } from '../../components/ui/Select';
import { Modal } from '../../components/ui/Modal';
import { FileText, Download, Play } from 'lucide-react';

export const PayrollPage: React.FC = () => {
  const [payrolls, setPayrolls] = useState<Payroll[]>([]);
  const [selectedPayrollItems, setSelectedPayrollItems] = useState<PayrollItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Modal states
  const [isGenerateModalOpen, setIsGenerateModalOpen] = useState(false);
  const [isPayslipModalOpen, setIsPayslipModalOpen] = useState(false);
  const [activePayslipItem, setActivePayslipItem] = useState<PayrollItem | null>(null);

  const [month, setMonth] = useState('7'); // July
  const [year, setYear] = useState('2026');
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    fetchPayrolls();
  }, []);

  const fetchPayrolls = async () => {
    try {
      const res = await api.get<ApiResponse<any>>('/payroll/history');
      if (res.data.success) {
        const data = res.data.data;
        if (Array.isArray(data)) {
          setPayrolls(data);
        } else if (data && Array.isArray(data.content)) {
          setPayrolls(data.content);
        } else {
          setPayrolls([]);
        }
      }
    } catch (err) {
      console.error('Failed to fetch payroll history', err);
      setPayrolls([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGeneratePayroll = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);
    try {
      const res = await api.post<ApiResponse<Payroll>>(`/payroll/generate?month=${month}&year=${year}`, {
        month: Number(month),
        year: Number(year),
      });
      if (res.data.success) {
        setIsGenerateModalOpen(false);
        fetchPayrolls();
      }
    } catch (err) {
      console.error('Failed to generate payroll', err);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleViewPayrollDetails = async (payrollId: string) => {
    try {
      const res = await api.get<ApiResponse<any>>(`/payroll/${payrollId}/items`);
      if (res.data.success) {
        const data = res.data.data;
        const items = Array.isArray(data) ? data : (data?.content || []);
        setSelectedPayrollItems(items);
        if (items.length > 0) {
          setActivePayslipItem(items[0]);
          setIsPayslipModalOpen(true);
        }
      }
    } catch (err) {
      console.error('Failed to fetch payroll items', err);
    }
  };

  const handleDownloadPdf = (payrollItemId: string) => {
    const url = `http://localhost:8080/api/v1/reports/payslip/${payrollItemId}/pdf`;
    window.open(url, '_blank');
  };

  const safePayrolls = Array.isArray(payrolls) ? payrolls : [];

  const columns = [
    {
      header: 'Pay Period',
      accessor: (p: Payroll) => (
        <span className="font-bold text-slate-900 dark:text-slate-100">
          {p.month} / {p.year}
        </span>
      ),
    },
    { header: 'Employees', accessor: (p: Payroll) => p.totalEmployeesProcessed ?? p.totalEmployees ?? 0 },
    {
      header: 'Gross Salary',
      accessor: (p: Payroll) => (
        <span className="font-medium text-slate-700 dark:text-slate-200">
          ${(p.totalGrossSalary || 0).toLocaleString()}
        </span>
      ),
    },
    {
      header: 'Total Deductions',
      accessor: (p: Payroll) => (
        <span className="font-medium text-rose-500">
          -${(p.totalDeductions || 0).toLocaleString()}
        </span>
      ),
    },
    {
      header: 'Net Payable',
      accessor: (p: Payroll) => (
        <span className="font-bold text-emerald-600 dark:text-emerald-400">
          ${(p.totalNetSalary || 0).toLocaleString()}
        </span>
      ),
    },
    {
      header: 'Status',
      accessor: (p: Payroll) => (
        <Badge variant={p.status === 'PAID' ? 'success' : 'warning'}>{p.status || 'PROCESSED'}</Badge>
      ),
    },
    {
      header: 'Actions',
      accessor: (p: Payroll) => (
        <Button size="sm" variant="outline" onClick={() => handleViewPayrollDetails(p.id)}>
          <FileText className="w-3.5 h-3.5 mr-1" />
          View Slips
        </Button>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
            Payroll Engine
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Automated monthly salary calculation, PF/ESI tax compliance, and PDF payslip generation.
          </p>
        </div>
        <Button onClick={() => setIsGenerateModalOpen(true)} leftIcon={<Play className="w-4 h-4" />}>
          Run Payroll Batch
        </Button>
      </div>

      {/* Payroll History Table */}
      <div>
        <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100 mb-3">Payroll Run History</h3>
        <Table columns={columns} data={safePayrolls} keyExtractor={(p) => p.id} isLoading={isLoading} />
      </div>

      {/* Run Payroll Modal */}
      <Modal isOpen={isGenerateModalOpen} onClose={() => setIsGenerateModalOpen(false)} title="Run Monthly Payroll Engine">
        <form onSubmit={handleGeneratePayroll} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Month"
              value={month}
              onChange={(e) => setMonth(e.target.value)}
              options={[
                { value: '1', label: 'January' },
                { value: '2', label: 'February' },
                { value: '3', label: 'March' },
                { value: '4', label: 'April' },
                { value: '5', label: 'May' },
                { value: '6', label: 'June' },
                { value: '7', label: 'July' },
                { value: '8', label: 'August' },
                { value: '9', label: 'September' },
                { value: '10', label: 'October' },
                { value: '11', label: 'November' },
                { value: '12', label: 'December' },
              ]}
            />
            <Select
              label="Year"
              value={year}
              onChange={(e) => setYear(e.target.value)}
              options={[
                { value: '2026', label: '2026' },
                { value: '2025', label: '2025' },
              ]}
            />
          </div>
          <div className="p-3 rounded-lg bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 text-xs text-indigo-700 dark:text-indigo-300">
            Payroll calculation will apply standard allowances (HRA, DA), statutory deductions (PF 12%, ESI 0.75%, Professional Tax), and income tax slabs.
          </div>
          <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsGenerateModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isProcessing}>
              Execute Payroll Engine
            </Button>
          </div>
        </form>
      </Modal>

      {/* View Payslip Breakdown Modal */}
      {isPayslipModalOpen && activePayslipItem && (
        <Modal isOpen={isPayslipModalOpen} onClose={() => setIsPayslipModalOpen(false)} title="Employee Salary Slip Breakdown" maxWidth="xl">
          <div className="space-y-6">
            {/* Select Employee Slip Dropdown if multiple items */}
            {selectedPayrollItems.length > 1 && (
              <div className="flex items-center gap-3 bg-slate-100 dark:bg-slate-800 p-3 rounded-xl">
                <span className="text-xs font-bold text-slate-600 dark:text-slate-300">Select Employee:</span>
                <select
                  value={activePayslipItem.id}
                  onChange={(e) => {
                    const found = selectedPayrollItems.find((item) => item.id === e.target.value);
                    if (found) setActivePayslipItem(found);
                  }}
                  className="bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 text-slate-900 dark:text-slate-100 rounded-lg px-3 py-1 text-sm flex-1"
                >
                  {selectedPayrollItems.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.employeeName} ({item.employeeCode})
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div className="flex items-center justify-between p-4 rounded-xl bg-slate-100 dark:bg-slate-800">
              <div>
                <h4 className="font-bold text-slate-900 dark:text-slate-100">{activePayslipItem.employeeName}</h4>
                <p className="text-xs text-slate-400 font-mono">{activePayslipItem.employeeCode} &bull; {activePayslipItem.departmentName || 'Engineering'}</p>
              </div>
              <Button size="sm" onClick={() => handleDownloadPdf(activePayslipItem.id)} leftIcon={<Download className="w-4 h-4" />}>
                Download PDF Slip
              </Button>
            </div>

            <div className="grid grid-cols-2 gap-6 text-sm">
              {/* Earnings */}
              <div className="p-4 rounded-xl border border-emerald-200 dark:border-emerald-900/50 bg-emerald-50/50 dark:bg-emerald-950/20">
                <h5 className="font-bold text-emerald-700 dark:text-emerald-400 border-b border-emerald-200 dark:border-emerald-800/60 pb-2 mb-3">EARNINGS</h5>
                <div className="space-y-2 text-xs">
                  <div className="flex justify-between"><span>Basic Salary</span><span className="font-mono">${activePayslipItem.basicSalary}</span></div>
                  <div className="flex justify-between"><span>House Rent Allowance (HRA)</span><span className="font-mono">${activePayslipItem.hra}</span></div>
                  <div className="flex justify-between"><span>Dearness Allowance (DA)</span><span className="font-mono">${activePayslipItem.da}</span></div>
                  <div className="flex justify-between"><span>Special Allowance</span><span className="font-mono">${activePayslipItem.specialAllowance}</span></div>
                  <div className="flex justify-between border-t border-emerald-200 dark:border-emerald-800/60 pt-2 font-bold text-slate-900 dark:text-slate-100">
                    <span>GROSS SALARY</span><span className="font-mono">${activePayslipItem.grossSalary}</span>
                  </div>
                </div>
              </div>

              {/* Deductions */}
              <div className="p-4 rounded-xl border border-rose-200 dark:border-rose-900/50 bg-rose-50/50 dark:bg-rose-950/20">
                <h5 className="font-bold text-rose-700 dark:text-rose-400 border-b border-rose-200 dark:border-rose-800/60 pb-2 mb-3">DEDUCTIONS</h5>
                <div className="space-y-2 text-xs">
                  <div className="flex justify-between"><span>Provident Fund (PF)</span><span className="font-mono">${activePayslipItem.pfDeduction}</span></div>
                  <div className="flex justify-between"><span>ESI Contribution</span><span className="font-mono">${activePayslipItem.esiDeduction}</span></div>
                  <div className="flex justify-between"><span>Professional Tax</span><span className="font-mono">${activePayslipItem.professionalTax}</span></div>
                  <div className="flex justify-between"><span>Income Tax (TDS)</span><span className="font-mono">${activePayslipItem.incomeTaxTds}</span></div>
                  <div className="flex justify-between border-t border-rose-200 dark:border-rose-800/60 pt-2 font-bold text-slate-900 dark:text-slate-100">
                    <span>TOTAL DEDUCTIONS</span><span className="font-mono">${activePayslipItem.totalDeductions}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Net Salary Highlight */}
            <div className="p-4 rounded-xl bg-indigo-600 text-white flex items-center justify-between shadow-lg">
              <span className="font-bold uppercase tracking-wider text-xs">NET SALARY PAYABLE</span>
              <span className="font-black text-2xl font-mono">${activePayslipItem.netSalary}</span>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
