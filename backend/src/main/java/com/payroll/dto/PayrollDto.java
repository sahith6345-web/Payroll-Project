package com.payroll.dto;

import com.payroll.common.PayrollStatus;
import com.payroll.common.TaxRegime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PayrollDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProcessRequest {
        @NotNull
        @Min(1) @Max(12)
        private Integer month;

        @NotNull
        private Integer year;

        private String departmentId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalaryStructureRequest {
        @NotNull
        private String employeeId;
        private BigDecimal basicSalary;
        private BigDecimal houseRentAllowance;
        private BigDecimal dearnessAllowance;
        private BigDecimal specialAllowance;
        private BigDecimal medicalAllowance;
        private BigDecimal transportAllowance;
        private BigDecimal pfPercentage;
        private BigDecimal esiPercentage;
        private BigDecimal professionalTax;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PayrollSummaryResponse {
        private String id;
        private int month;
        private int year;
        private String monthYear;
        private PayrollStatus status;
        private int totalEmployeesProcessed;
        private BigDecimal totalGrossSalary;
        private BigDecimal totalDeductions;
        private BigDecimal totalNetSalary;
        private LocalDate processingDate;
        private LocalDate paymentDate;
        private String approvedByName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PayslipResponse {
        private String id;
        private String payrollId;
        private String employeeId;
        private String employeeCode;
        private String employeeName;
        private String departmentName;
        private String designationTitle;
        private int month;
        private int year;
        private int totalWorkingDays;
        private double presentDays;
        private double leaveDays;
        private double unpaidLeaveDays;
        private double overtimeHours;
        private BigDecimal basicSalary;
        private BigDecimal hra;
        private BigDecimal da;
        private BigDecimal specialAllowance;
        private BigDecimal overtimePay;
        private BigDecimal bonus;
        private BigDecimal reimbursements;
        private BigDecimal grossSalary;
        private BigDecimal pfDeduction;
        private BigDecimal esiDeduction;
        private BigDecimal professionalTax;
        private BigDecimal incomeTaxTds;
        private BigDecimal loanEmiDeduction;
        private BigDecimal unpaidLeaveDeduction;
        private BigDecimal lateDeduction;
        private BigDecimal totalDeductions;
        private BigDecimal netSalary;
        private TaxRegime taxRegime;
        private String bankAccountNumber;
        private String payslipPdfUrl;
    }
}
