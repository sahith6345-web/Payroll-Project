export type RoleType = 'SUPER_ADMIN' | 'ADMIN' | 'HR' | 'PAYROLL_MANAGER' | 'MANAGER' | 'EMPLOYEE';

export interface User {
  id: string;
  email: string;
  username: string;
  firstName: string;
  lastName: string;
  employeeId?: string;
  roles: RoleType[];
  profilePictureUrl?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: User;
}

export interface Employee {
  id: string;
  userId?: string;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  departmentId?: string;
  departmentName?: string;
  designationId?: string;
  designationTitle?: string;
  joiningDate?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'TERMINATED' | 'ON_LEAVE';
  profilePictureUrl?: string;
  bankName?: string;
  accountNumber?: string;
  ifscCode?: string;
  panNumber?: string;
  taxRegime?: 'OLD' | 'NEW';
}

export interface Department {
  id: string;
  code: string;
  name: string;
  description?: string;
  managerId?: string;
  active: boolean;
}

export interface Designation {
  id: string;
  title: string;
  code: string;
  departmentId: string;
  baseSalaryMin?: number;
  baseSalaryMax?: number;
  active: boolean;
}

export interface Attendance {
  id: string;
  employeeId: string;
  date: string;
  checkIn?: string;
  checkOut?: string;
  status: 'PRESENT' | 'ABSENT' | 'HALF_DAY' | 'LATE' | 'OVERTIME';
  workHours?: number;
}

export interface LeaveRequest {
  id: string;
  employeeId: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  numberOfDays: number;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  appliedDate: string;
  approvedBy?: string;
  approvalComments?: string;
}

export interface Holiday {
  id: string;
  title: string;
  date: string;
  type: string;
  description?: string;
}

export interface SalaryStructure {
  id: string;
  employeeId: string;
  basicSalary: number;
  hra: number;
  da: number;
  specialAllowance: number;
  medicalAllowance: number;
  pfRate: number;
  esiRate: number;
  taxRegime: 'OLD' | 'NEW';
  effectiveFrom: string;
  active: boolean;
}

export interface Payroll {
  id: string;
  month: number;
  year: number;
  totalEmployees?: number;
  totalEmployeesProcessed?: number;
  totalGrossSalary: number;
  totalDeductions: number;
  totalNetSalary: number;
  status: 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'PAID';
  processedDate?: string;
  locked?: boolean;
}

export interface PayrollItem {
  id: string;
  payrollId: string;
  employeeId: string;
  employeeCode: string;
  employeeName: string;
  departmentName?: string;
  designationTitle?: string;
  month: number;
  year: number;
  basicSalary: number;
  hra: number;
  da: number;
  specialAllowance: number;
  medicalAllowance: number;
  grossSalary: number;
  pfDeduction: number;
  esiDeduction: number;
  professionalTax: number;
  incomeTaxTds: number;
  loanEmiDeduction: number;
  unpaidLeaveDeduction: number;
  overtimePay: number;
  bonus: number;
  totalDeductions: number;
  netSalary: number;
  status: 'GENERATED' | 'PAID';
}

export interface Reimbursement {
  id: string;
  employeeId: string;
  title: string;
  description?: string;
  expenseType: string;
  amount: number;
  expenseDate: string;
  receiptUrl?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'PAID';
  appliedDate: string;
  approvedBy?: string;
  comments?: string;
}

export interface AuditLog {
  id: string;
  action: string;
  performedBy: string;
  details: string;
  timestamp: string;
  ipAddress?: string;
}

export interface DashboardStats {
  totalEmployees: number;
  activeEmployees: number;
  totalDepartments: number;
  presentToday: number;
  absentToday: number;
  pendingLeaves: number;
  totalPayrollDisbursed: number;
  currentMonth: string;
  monthlyPayrollSummary: { month: string; amount: number }[];
  departmentBreakdown: { name: string; count: number }[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
