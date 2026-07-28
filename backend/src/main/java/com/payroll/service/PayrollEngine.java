package com.payroll.service;

import com.payroll.common.TaxRegime;
import com.payroll.entity.Attendance;
import com.payroll.entity.Loan;
import com.payroll.entity.PayrollItem;
import com.payroll.entity.SalaryStructure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PayrollEngine {

    /**
     * Calculates monthly payslip item for an employee.
     */
    public static PayrollItem calculateMonthlyPayslip(
            SalaryStructure structure,
            List<Attendance> monthlyAttendance,
            List<Loan> activeLoans,
            double unpaidLeaveDays,
            BigDecimal bonus,
            BigDecimal reimbursements,
            TaxRegime taxRegime,
            int month,
            int year,
            int totalWorkingDays
    ) {
        BigDecimal basic = defaultZero(structure.getBasicSalary());
        BigDecimal hra = defaultZero(structure.getHouseRentAllowance());
        BigDecimal da = defaultZero(structure.getDearnessAllowance());
        BigDecimal special = defaultZero(structure.getSpecialAllowance());
        
        // Calculate Overtime
        double totalOvertimeHours = monthlyAttendance.stream()
                .mapToDouble(Attendance::getOvertimeHours)
                .sum();
        
        // Hourly rate = Basic / (20 days * 8 hrs = 160)
        BigDecimal hourlyRate = basic.divide(BigDecimal.valueOf(160), 2, RoundingMode.HALF_UP);
        BigDecimal overtimePay = hourlyRate.multiply(BigDecimal.valueOf(totalOvertimeHours)).multiply(BigDecimal.valueOf(1.5));

        // Monthly Base Gross
        BigDecimal baseGross = basic.add(hra).add(da).add(special);

        // Deductions: Unpaid Leave
        BigDecimal dailyRate = baseGross.divide(BigDecimal.valueOf(totalWorkingDays > 0 ? totalWorkingDays : 22), 2, RoundingMode.HALF_UP);
        BigDecimal unpaidLeaveDeduction = dailyRate.multiply(BigDecimal.valueOf(unpaidLeaveDays));

        // Actual Gross Salary for the month
        BigDecimal actualGross = baseGross.add(overtimePay).add(defaultZero(bonus)).add(defaultZero(reimbursements)).subtract(unpaidLeaveDeduction);
        if (actualGross.compareTo(BigDecimal.ZERO) < 0) {
            actualGross = BigDecimal.ZERO;
        }

        // Statutory Deductions: PF (12% of Basic)
        BigDecimal pfRate = structure.getPfPercentage() != null ? structure.getPfPercentage() : BigDecimal.valueOf(12.0);
        BigDecimal pfDeduction = basic.multiply(pfRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Statutory Deductions: ESI (0.75% of Gross if applicable)
        BigDecimal esiDeduction = BigDecimal.ZERO;
        if (structure.getEsiPercentage() != null && structure.getEsiPercentage().compareTo(BigDecimal.ZERO) > 0) {
            esiDeduction = actualGross.multiply(structure.getEsiPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Professional Tax
        BigDecimal professionalTax = defaultZero(structure.getProfessionalTax());

        // Loan EMI Auto Deduction
        BigDecimal totalLoanEmi = activeLoans.stream()
                .map(Loan::getMonthlyEmi)
                .filter(emi -> emi != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // TDS Income Tax calculation
        BigDecimal annualGross = actualGross.multiply(BigDecimal.valueOf(12));
        BigDecimal estimatedTds = calculateTds(annualGross, taxRegime).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        // Total Deductions
        BigDecimal totalDeductions = pfDeduction
                .add(esiDeduction)
                .add(professionalTax)
                .add(estimatedTds)
                .add(totalLoanEmi)
                .add(unpaidLeaveDeduction);

        // Net Salary
        BigDecimal netSalary = actualGross.subtract(pfDeduction).subtract(esiDeduction).subtract(professionalTax).subtract(estimatedTds).subtract(totalLoanEmi);
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            netSalary = BigDecimal.ZERO;
        }

        double presentDays = monthlyAttendance.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().name().equals("PRESENT"))
                .count();

        return PayrollItem.builder()
                .employeeId(structure.getEmployeeId())
                .employeeCode(structure.getEmployeeCode())
                .month(month)
                .year(year)
                .totalWorkingDays(totalWorkingDays)
                .presentDays(presentDays)
                .unpaidLeaveDays(unpaidLeaveDays)
                .overtimeHours(totalOvertimeHours)
                .basicSalary(basic)
                .hra(hra)
                .da(da)
                .specialAllowance(special)
                .overtimePay(overtimePay)
                .bonus(defaultZero(bonus))
                .reimbursements(defaultZero(reimbursements))
                .grossSalary(actualGross)
                .pfDeduction(pfDeduction)
                .esiDeduction(esiDeduction)
                .professionalTax(professionalTax)
                .incomeTaxTds(estimatedTds)
                .loanEmiDeduction(totalLoanEmi)
                .unpaidLeaveDeduction(unpaidLeaveDeduction)
                .totalDeductions(totalDeductions)
                .netSalary(netSalary)
                .taxRegime(taxRegime != null ? taxRegime : TaxRegime.NEW_REGIME)
                .build();
    }

    private static BigDecimal calculateTds(BigDecimal annualGross, TaxRegime regime) {
        if (annualGross == null || annualGross.compareTo(BigDecimal.valueOf(500000)) <= 0) {
            return BigDecimal.ZERO;
        }
        // Standard Tax Slabs Engine (New Regime: 0-3L nil, 3-6L 5%, 6-9L 10%, 9-12L 15%, 12-15L 20%, >15L 30%)
        BigDecimal tax = BigDecimal.ZERO;
        double annual = annualGross.doubleValue();

        if (annual > 1500000) {
            tax = tax.add(BigDecimal.valueOf((annual - 1500000) * 0.30));
            annual = 1500000;
        }
        if (annual > 1200000) {
            tax = tax.add(BigDecimal.valueOf((annual - 1200000) * 0.20));
            annual = 1200000;
        }
        if (annual > 900000) {
            tax = tax.add(BigDecimal.valueOf((annual - 900000) * 0.15));
            annual = 900000;
        }
        if (annual > 600000) {
            tax = tax.add(BigDecimal.valueOf((annual - 600000) * 0.10));
            annual = 600000;
        }
        if (annual > 300000) {
            tax = tax.add(BigDecimal.valueOf((annual - 300000) * 0.05));
        }

        return tax;
    }

    private static BigDecimal defaultZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
