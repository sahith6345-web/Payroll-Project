package com.payroll.service;

import com.payroll.common.PayrollStatus;
import com.payroll.dto.PayrollDto;
import com.payroll.entity.*;
import com.payroll.pdf.PayslipPdfGenerator;
import com.payroll.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final AttendanceRepository attendanceRepository;
    private final LoanRepository loanRepository;

    public PayrollDto.PayrollSummaryResponse processMonthlyPayroll(PayrollDto.ProcessRequest request) {
        String monthYear = String.format("%d-%02d", request.getYear(), request.getMonth());

        Payroll masterPayroll = payrollRepository.findByMonthYear(monthYear)
                .orElseGet(() -> Payroll.builder()
                        .month(request.getMonth())
                        .year(request.getYear())
                        .monthYear(monthYear)
                        .status(PayrollStatus.DRAFT)
                        .build());

        List<Employee> employees = employeeRepository.findByStatus("ACTIVE");
        List<PayrollItem> items = new ArrayList<>();

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;

        for (Employee emp : employees) {
            SalaryStructure structure = salaryStructureRepository.findByEmployeeId(emp.getId())
                    .orElseGet(() -> SalaryStructure.builder()
                            .employeeId(emp.getId())
                            .employeeCode(emp.getEmployeeCode())
                            .basicSalary(BigDecimal.valueOf(5000.00))
                            .houseRentAllowance(BigDecimal.valueOf(2000.00))
                            .dearnessAllowance(BigDecimal.valueOf(500.00))
                            .specialAllowance(BigDecimal.valueOf(1000.00))
                            .pfPercentage(BigDecimal.valueOf(12.0))
                            .professionalTax(BigDecimal.valueOf(200.00))
                            .build());

            LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            List<Attendance> attendanceLogs = attendanceRepository.findByEmployeeIdAndDateBetween(emp.getId(), startDate, endDate);
            List<Loan> activeLoans = loanRepository.findByEmployeeIdAndStatus(emp.getId(), com.payroll.common.LoanStatus.ACTIVE);

            PayrollItem item = PayrollEngine.calculateMonthlyPayslip(
                    structure,
                    attendanceLogs,
                    activeLoans,
                    0.0, // unpaid leave days
                    BigDecimal.ZERO, // bonus
                    BigDecimal.ZERO, // reimbursements
                    com.payroll.common.TaxRegime.NEW_REGIME,
                    request.getMonth(),
                    request.getYear(),
                    22 // working days
            );

            item.setPayrollId(masterPayroll.getId() != null ? masterPayroll.getId() : "TEMP");
            item.setEmployeeName(emp.getFirstName() + " " + emp.getLastName());
            item.setDepartmentName(emp.getDepartmentName());
            item.setDesignationTitle(emp.getDesignationTitle());
            item.setBankAccountNumber(emp.getBankDetails() != null ? emp.getBankDetails().getAccountNumber() : "N/A");

            items.add(item);

            totalGross = totalGross.add(item.getGrossSalary());
            totalDeductions = totalDeductions.add(item.getTotalDeductions());
            totalNet = totalNet.add(item.getNetSalary());
        }

        masterPayroll.setTotalEmployeesProcessed(employees.size());
        masterPayroll.setTotalGrossSalary(totalGross);
        masterPayroll.setTotalDeductions(totalDeductions);
        masterPayroll.setTotalNetSalary(totalNet);
        masterPayroll.setProcessingDate(LocalDate.now());
        masterPayroll.setStatus(PayrollStatus.PENDING_APPROVAL);

        Payroll savedMaster = payrollRepository.save(masterPayroll);

        // Update payroll items with saved master ID
        for (PayrollItem item : items) {
            item.setPayrollId(savedMaster.getId());
            payrollItemRepository.save(item);
        }

        return mapToSummary(savedMaster);
    }

    public List<PayrollDto.PayrollSummaryResponse> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<PayrollDto.PayslipResponse> getPayslipsForPayroll(String payrollId) {
        return payrollItemRepository.findByPayrollId(payrollId).stream()
                .map(this::mapToItemResponse)
                .toList();
    }

    public List<PayrollDto.PayslipResponse> getPayslipsForEmployee(String employeeId) {
        return payrollItemRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToItemResponse)
                .toList();
    }

    public byte[] downloadPayslipPdf(String payslipId) throws IOException {
        PayrollItem item = payrollItemRepository.findById(payslipId)
                .orElseThrow(() -> new RuntimeException("Payslip not found with id: " + payslipId));

        return PayslipPdfGenerator.generatePayslipPdf(item, "Enterprise HRMS & Payroll Inc.", "100 Innovation Way, Tech Park, Suite 400");
    }

    private PayrollDto.PayrollSummaryResponse mapToSummary(Payroll p) {
        return PayrollDto.PayrollSummaryResponse.builder()
                .id(p.getId())
                .month(p.getMonth())
                .year(p.getYear())
                .monthYear(p.getMonthYear())
                .status(p.getStatus())
                .totalEmployeesProcessed(p.getTotalEmployeesProcessed())
                .totalGrossSalary(p.getTotalGrossSalary())
                .totalDeductions(p.getTotalDeductions())
                .totalNetSalary(p.getTotalNetSalary())
                .processingDate(p.getProcessingDate())
                .paymentDate(p.getPaymentDate())
                .approvedByName(p.getApprovedByName())
                .build();
    }

    private PayrollDto.PayslipResponse mapToItemResponse(PayrollItem i) {
        return PayrollDto.PayslipResponse.builder()
                .id(i.getId())
                .payrollId(i.getPayrollId())
                .employeeId(i.getEmployeeId())
                .employeeCode(i.getEmployeeCode())
                .employeeName(i.getEmployeeName())
                .departmentName(i.getDepartmentName())
                .designationTitle(i.getDesignationTitle())
                .month(i.getMonth())
                .year(i.getYear())
                .totalWorkingDays(i.getTotalWorkingDays())
                .presentDays(i.getPresentDays())
                .unpaidLeaveDays(i.getUnpaidLeaveDays())
                .overtimeHours(i.getOvertimeHours())
                .basicSalary(i.getBasicSalary())
                .hra(i.getHra())
                .da(i.getDa())
                .specialAllowance(i.getSpecialAllowance())
                .overtimePay(i.getOvertimePay())
                .bonus(i.getBonus())
                .reimbursements(i.getReimbursements())
                .grossSalary(i.getGrossSalary())
                .pfDeduction(i.getPfDeduction())
                .esiDeduction(i.getEsiDeduction())
                .professionalTax(i.getProfessionalTax())
                .incomeTaxTds(i.getIncomeTaxTds())
                .loanEmiDeduction(i.getLoanEmiDeduction())
                .unpaidLeaveDeduction(i.getUnpaidLeaveDeduction())
                .totalDeductions(i.getTotalDeductions())
                .netSalary(i.getNetSalary())
                .taxRegime(i.getTaxRegime())
                .bankAccountNumber(i.getBankAccountNumber())
                .payslipPdfUrl(i.getPayslipPdfUrl())
                .build();
    }
}
