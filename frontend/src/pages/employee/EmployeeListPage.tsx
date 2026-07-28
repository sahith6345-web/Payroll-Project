import React, { useEffect, useState } from 'react';
import api from '../../services/api';
import { ApiResponse, Employee, Department, PayrollItem } from '../../types';
import { Card } from '../../components/ui/Card';
import { Table } from '../../components/ui/Table';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Select } from '../../components/ui/Select';
import { Badge } from '../../components/ui/Badge';
import { Modal } from '../../components/ui/Modal';
import { Search, UserPlus, Eye, DollarSign, CheckCircle2, AlertCircle, FileText, Download } from 'lucide-react';

export const EmployeeListPage: React.FC = () => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // Add Employee Modal State
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isSubmittingEmp, setIsSubmittingEmp] = useState(false);
  const [addEmpError, setAddEmpError] = useState('');
  const [addEmpSuccess, setAddEmpSuccess] = useState('');

  // View Profile & Salary Paid History Modal State
  const [selectedEmp, setSelectedEmp] = useState<Employee | null>(null);
  const [userPayslips, setUserPayslips] = useState<PayrollItem[]>([]);
  const [isLoadingPayslips, setIsLoadingPayslips] = useState(false);

  // Salary Structure Modal State
  const [salaryEmp, setSalaryEmp] = useState<Employee | null>(null);
  const [isSalaryModalOpen, setIsSalaryModalOpen] = useState(false);
  const [isSavingSalary, setIsSavingSalary] = useState(false);
  const [salarySuccessMsg, setSalarySuccessMsg] = useState('');
  const [salaryForm, setSalaryForm] = useState({
    basicSalary: 6000,
    houseRentAllowance: 2400,
    dearnessAllowance: 600,
    specialAllowance: 1000,
    medicalAllowance: 250,
    pfPercentage: 12,
    professionalTax: 200,
  });

  // Form State
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    departmentId: '',
    designationId: '',
    bankName: 'JPMorgan Chase',
    accountNumber: '1029384756',
    ifscCode: 'CHAS00192',
    panNumber: 'ABCDE1234F',
    initialBasicSalary: 6000,
  });

  useEffect(() => {
    fetchEmployees();
    fetchDepartments();
  }, []);

  const fetchEmployees = async () => {
    try {
      const res = await api.get<ApiResponse<any>>('/employees?size=100');
      if (res.data.success) {
        const data = res.data.data;
        if (Array.isArray(data)) {
          setEmployees(data);
        } else if (data && Array.isArray(data.content)) {
          setEmployees(data.content);
        } else {
          setEmployees([]);
        }
      }
    } catch (err) {
      console.error('Failed to fetch employees', err);
      setEmployees([]);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const res = await api.get<ApiResponse<Department[]>>('/departments');
      if (res.data.success) {
        setDepartments(res.data.data);
      }
    } catch (err) {
      console.error('Failed to fetch departments', err);
    }
  };

  const handleCreateEmployee = async (e: React.FormEvent) => {
    e.preventDefault();
    setAddEmpError('');
    setAddEmpSuccess('');
    if (!formData.firstName.trim() || !formData.lastName.trim() || !formData.email.trim()) {
      setAddEmpError('First name, last name, and email are required.');
      return;
    }

    setIsSubmittingEmp(true);
    try {
      // Only send fields that match the backend EmployeeRequest DTO
      const payload: any = {
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        email: formData.email.trim(),
        phone: formData.phone.trim() || '+1 555-0199',
      };

      // Only include departmentId if selected
      if (formData.departmentId) {
        payload.departmentId = formData.departmentId;
      }

      // Include bank details
      payload.bankDetails = {
        bankName: formData.bankName,
        accountNumber: formData.accountNumber,
        ifscCode: formData.ifscCode,
        panNumber: formData.panNumber,
      };

      const res = await api.post<ApiResponse<Employee>>('/employees', payload);
      if (res.data.success && res.data.data?.id) {
        setAddEmpSuccess(`Employee ${formData.firstName} ${formData.lastName} created successfully!`);
        setTimeout(() => {
          setIsAddModalOpen(false);
          setAddEmpSuccess('');
          setFormData({
            firstName: '',
            lastName: '',
            email: '',
            phone: '',
            departmentId: '',
            designationId: '',
            bankName: 'JPMorgan Chase',
            accountNumber: '1029384756',
            ifscCode: 'CHAS00192',
            panNumber: 'ABCDE1234F',
            initialBasicSalary: 6000,
          });
          fetchEmployees();
        }, 1200);
      } else {
        setAddEmpError(res.data.message || 'Failed to create employee');
      }
    } catch (err: any) {
      console.error('Error creating employee', err);
      const msg = err.response?.data?.message || err.message || 'Server error creating employee. Check fields.';
      setAddEmpError(msg);
    } finally {
      setIsSubmittingEmp(false);
    }
  };

  const handleViewProfileAndSalary = async (emp: Employee) => {
    setSelectedEmp(emp);
    setIsLoadingPayslips(true);
    setUserPayslips([]);
    try {
      const res = await api.get<ApiResponse<any>>(`/payroll/employee/${emp.id}/payslips`);
      if (res.data.success) {
        const data = res.data.data;
        setUserPayslips(Array.isArray(data) ? data : (data?.content || []));
      }
    } catch (err) {
      console.error('Failed to fetch user payslips', err);
    } finally {
      setIsLoadingPayslips(false);
    }
  };

  const handleOpenSalaryModal = async (emp: Employee) => {
    setSalaryEmp(emp);
    setIsSalaryModalOpen(true);
    setSalarySuccessMsg('');
    try {
      const res = await api.get<ApiResponse<any>>(`/salary/${emp.id}`);
      if (res.data.success && res.data.data) {
        const s = res.data.data;
        setSalaryForm({
          basicSalary: s.basicSalary ?? 6000,
          houseRentAllowance: s.houseRentAllowance ?? 2400,
          dearnessAllowance: s.dearnessAllowance ?? 600,
          specialAllowance: s.specialAllowance ?? 1000,
          medicalAllowance: s.medicalAllowance ?? 250,
          pfPercentage: s.pfPercentage ?? 12,
          professionalTax: s.professionalTax ?? 200,
        });
      }
    } catch (err) {
      console.error('Failed to load salary structure', err);
    }
  };

  const handleSaveSalary = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!salaryEmp) return;
    setIsSavingSalary(true);
    try {
      await api.post(`/salary/${salaryEmp.id}`, salaryForm);
      setSalarySuccessMsg(`Salary structure for ${salaryEmp.firstName} updated successfully!`);
      setTimeout(() => {
        setIsSalaryModalOpen(false);
        setSalarySuccessMsg('');
      }, 1500);
    } catch (err) {
      console.error('Failed to save salary', err);
    } finally {
      setIsSavingSalary(false);
    }
  };

  const handleDownloadPdf = (payrollItemId: string) => {
    const url = `http://localhost:8080/api/v1/reports/payslip/${payrollItemId}/pdf`;
    window.open(url, '_blank');
  };

  const filtered = Array.isArray(employees)
    ? employees.filter((emp) => {
        const matchesSearch = `${emp.firstName || ''} ${emp.lastName || ''} ${emp.employeeCode || ''} ${emp.email || ''}`
          .toLowerCase()
          .includes(search.toLowerCase());
        const matchesDept = !departmentFilter || emp.departmentId === departmentFilter;
        const matchesStatus = !statusFilter || emp.status === statusFilter;
        return matchesSearch && matchesDept && matchesStatus;
      })
    : [];

  const columns = [
    {
      header: 'Employee Code',
      accessor: (emp: Employee) => (
        <span className="font-mono text-xs font-bold text-indigo-600 dark:text-indigo-400">
          {emp.employeeCode}
        </span>
      ),
    },
    {
      header: 'Name',
      accessor: (emp: Employee) => (
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-indigo-600 text-white font-bold text-xs flex items-center justify-center">
            {emp.firstName?.charAt(0) || 'E'}
          </div>
          <div>
            <div className="font-semibold text-slate-900 dark:text-slate-100">
              {emp.firstName} {emp.lastName}
            </div>
            <div className="text-xs text-slate-400">{emp.email}</div>
          </div>
        </div>
      ),
    },
    {
      header: 'Department / Role',
      accessor: (emp: Employee) => (
        <div>
          <div className="text-slate-800 dark:text-slate-200 font-medium text-xs">
            {emp.departmentName || 'Engineering'}
          </div>
          <div className="text-[11px] text-slate-400">{emp.designationTitle || 'Staff Member'}</div>
        </div>
      ),
    },
    {
      header: 'Phone',
      accessor: (emp: Employee) => <span className="text-xs text-slate-500">{emp.phone || '+1 555-0199'}</span>,
    },
    {
      header: 'Status',
      accessor: (emp: Employee) => (
        <Badge variant={emp.status === 'ACTIVE' ? 'success' : 'danger'}>{emp.status || 'ACTIVE'}</Badge>
      ),
    },
    {
      header: 'Actions',
      accessor: (emp: Employee) => (
        <div className="flex items-center gap-2">
          <Button
            size="sm"
            variant="ghost"
            title="View Profile & Salary Paid History"
            onClick={() => handleViewProfileAndSalary(emp)}
          >
            <Eye className="w-4 h-4 text-indigo-500 mr-1" />
            Profile & Paid History
          </Button>
          <Button
            size="sm"
            variant="outline"
            className="text-emerald-600 border-emerald-300 hover:bg-emerald-50 dark:hover:bg-emerald-950"
            title="Provide / Edit Salary Structure"
            onClick={() => handleOpenSalaryModal(emp)}
          >
            <DollarSign className="w-3.5 h-3.5 mr-1" />
            Provide Salary
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 dark:text-slate-100 tracking-tight">
            Employee Directory
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Manage organization personnel, bank credentials, salary structures, and paid salary history.
          </p>
        </div>
        <Button onClick={() => setIsAddModalOpen(true)} leftIcon={<UserPlus className="w-4 h-4" />}>
          Add New Employee
        </Button>
      </div>

      {/* Filters Bar */}
      <Card className="flex flex-col sm:flex-row items-center gap-4">
        <div className="flex-1 w-full">
          <Input
            placeholder="Search by name, email, or code..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            leftIcon={<Search className="w-4 h-4" />}
          />
        </div>
        <div className="w-full sm:w-48">
          <Select
            value={departmentFilter}
            onChange={(e) => setDepartmentFilter(e.target.value)}
            options={[
              { value: '', label: 'All Departments' },
              ...departments.map((d) => ({ value: d.id, label: d.name })),
            ]}
          />
        </div>
        <div className="w-full sm:w-40">
          <Select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            options={[
              { value: '', label: 'All Statuses' },
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
            ]}
          />
        </div>
      </Card>

      {/* Employee Data Table */}
      <Table columns={columns} data={filtered} keyExtractor={(emp) => emp.id} isLoading={isLoading} />

      {/* Add Employee Modal */}
      <Modal isOpen={isAddModalOpen} onClose={() => setIsAddModalOpen(false)} title="Add New Employee">
        <form onSubmit={handleCreateEmployee} className="space-y-4">
          {addEmpError && (
            <div className="p-3 rounded-lg bg-rose-50 dark:bg-rose-950/40 border border-rose-200 dark:border-rose-800 text-xs text-rose-700 dark:text-rose-300 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-rose-500 shrink-0" />
              <span>{addEmpError}</span>
            </div>
          )}

          {addEmpSuccess && (
            <div className="p-3 rounded-lg bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800 text-xs text-emerald-700 dark:text-emerald-300 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" />
              <span>{addEmpSuccess}</span>
            </div>
          )}

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              required
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
            />
            <Input
              label="Last Name"
              required
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Email Address"
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
            <Input
              label="Phone Number"
              required
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Department"
              value={formData.departmentId}
              onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
              options={[
                { value: '', label: 'Select Department' },
                ...departments.map((d) => ({ value: d.id, label: d.name })),
              ]}
            />
            <Input
              label="Initial Basic Salary ($)"
              type="number"
              value={formData.initialBasicSalary}
              onChange={(e) => setFormData({ ...formData, initialBasicSalary: Number(e.target.value) })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Bank Account No."
              value={formData.accountNumber}
              onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
            />
            <Input
              label="Bank Name"
              value={formData.bankName}
              onChange={(e) => setFormData({ ...formData, bankName: e.target.value })}
            />
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
            <Button type="button" variant="outline" onClick={() => setIsAddModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isSubmittingEmp}>Save Employee</Button>
          </div>
        </form>
      </Modal>

      {/* Salary Structure Modal */}
      {isSalaryModalOpen && salaryEmp && (
        <Modal
          isOpen={isSalaryModalOpen}
          onClose={() => setIsSalaryModalOpen(false)}
          title={`Provide Salary Structure - ${salaryEmp.firstName} ${salaryEmp.lastName}`}
          maxWidth="lg"
        >
          <form onSubmit={handleSaveSalary} className="space-y-4">
            {salarySuccessMsg && (
              <div className="p-3 rounded-lg bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 text-emerald-700 dark:text-emerald-300 text-sm flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-500" />
                {salarySuccessMsg}
              </div>
            )}

            <div className="p-3 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs text-slate-600 dark:text-slate-300">
              Set fixed components for monthly payroll engine calculations.
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Basic Salary ($)"
                type="number"
                required
                value={salaryForm.basicSalary}
                onChange={(e) => setSalaryForm({ ...salaryForm, basicSalary: Number(e.target.value) })}
              />
              <Input
                label="House Rent Allowance (HRA) ($)"
                type="number"
                required
                value={salaryForm.houseRentAllowance}
                onChange={(e) => setSalaryForm({ ...salaryForm, houseRentAllowance: Number(e.target.value) })}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Dearness Allowance (DA) ($)"
                type="number"
                required
                value={salaryForm.dearnessAllowance}
                onChange={(e) => setSalaryForm({ ...salaryForm, dearnessAllowance: Number(e.target.value) })}
              />
              <Input
                label="Special Allowance ($)"
                type="number"
                required
                value={salaryForm.specialAllowance}
                onChange={(e) => setSalaryForm({ ...salaryForm, specialAllowance: Number(e.target.value) })}
              />
            </div>

            <div className="grid grid-cols-3 gap-4">
              <Input
                label="Medical Allowance ($)"
                type="number"
                value={salaryForm.medicalAllowance}
                onChange={(e) => setSalaryForm({ ...salaryForm, medicalAllowance: Number(e.target.value) })}
              />
              <Input
                label="PF Rate (%)"
                type="number"
                value={salaryForm.pfPercentage}
                onChange={(e) => setSalaryForm({ ...salaryForm, pfPercentage: Number(e.target.value) })}
              />
              <Input
                label="Professional Tax ($)"
                type="number"
                value={salaryForm.professionalTax}
                onChange={(e) => setSalaryForm({ ...salaryForm, professionalTax: Number(e.target.value) })}
              />
            </div>

            <div className="p-3 rounded-lg bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 text-xs flex justify-between font-bold text-indigo-700 dark:text-indigo-300">
              <span>Estimated Monthly Gross Salary:</span>
              <span className="font-mono">${salaryForm.basicSalary + salaryForm.houseRentAllowance + salaryForm.dearnessAllowance + salaryForm.specialAllowance} / mo</span>
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-slate-200 dark:border-slate-800">
              <Button type="button" variant="outline" onClick={() => setIsSalaryModalOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" isLoading={isSavingSalary}>
                Save Salary Structure
              </Button>
            </div>
          </form>
        </Modal>
      )}

      {/* View Profile & Salary Paid History Modal */}
      {selectedEmp && (
        <Modal isOpen={!!selectedEmp} onClose={() => setSelectedEmp(null)} title={`Employee Profile & Paid Salary History`} maxWidth="xl">
          <div className="space-y-6">
            <div className="flex items-center justify-between p-4 rounded-xl bg-slate-100 dark:bg-slate-800">
              <div className="flex items-center gap-4">
                <div className="w-14 h-14 rounded-full bg-indigo-600 text-white font-bold text-xl flex items-center justify-center">
                  {selectedEmp.firstName?.charAt(0) || 'E'}
                </div>
                <div>
                  <h3 className="text-lg font-bold text-slate-900 dark:text-slate-100">
                    {selectedEmp.firstName} {selectedEmp.lastName}
                  </h3>
                  <p className="text-xs text-indigo-500 font-mono font-bold">{selectedEmp.employeeCode} &bull; {selectedEmp.departmentName || 'Engineering'}</p>
                  <p className="text-xs text-slate-400">{selectedEmp.email}</p>
                </div>
              </div>
              <Badge variant={selectedEmp.status === 'ACTIVE' ? 'success' : 'danger'}>{selectedEmp.status || 'ACTIVE'}</Badge>
            </div>

            {/* Salary Paid History Section */}
            <div className="space-y-3">
              <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-2">
                <h4 className="font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                  <DollarSign className="w-4 h-4 text-emerald-500" />
                  Salary Paid History & Payslips
                </h4>
                <span className="text-xs text-slate-400">
                  Total Disbursed: <strong className="text-emerald-600 font-mono">${userPayslips.reduce((sum, item) => sum + (item.netSalary || 0), 0).toLocaleString()}</strong>
                </span>
              </div>

              {isLoadingPayslips ? (
                <div className="text-center py-6 text-xs text-slate-400">Loading salary payment history...</div>
              ) : userPayslips.length === 0 ? (
                <div className="text-center py-6 bg-slate-50 dark:bg-slate-900 rounded-xl text-xs text-slate-400">
                  No salary payments processed for this user yet. Execute the Payroll Engine batch to generate payslips.
                </div>
              ) : (
                <div className="space-y-2">
                  {userPayslips.map((slip) => (
                    <div key={slip.id} className="flex items-center justify-between p-3 rounded-lg border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 text-xs">
                      <div>
                        <span className="font-bold text-slate-900 dark:text-slate-100">Pay Period: {slip.month} / {slip.year}</span>
                        <div className="text-slate-400 text-[11px]">Gross: ${slip.grossSalary} &bull; Deductions: -${slip.totalDeductions}</div>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="font-bold font-mono text-emerald-600 dark:text-emerald-400 text-sm">${slip.netSalary}</span>
                        <Button size="sm" variant="ghost" onClick={() => handleDownloadPdf(slip.id)} leftIcon={<Download className="w-3.5 h-3.5 text-indigo-500" />}>
                          PDF
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
