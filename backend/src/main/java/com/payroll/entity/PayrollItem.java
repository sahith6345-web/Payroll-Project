package com.payroll.entity;

import com.payroll.common.BaseDocument;
import com.payroll.common.TaxRegime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payroll_items")
@CompoundIndex(def = "{'payrollId': 1, 'employeeId': 1}", unique = true)
public class PayrollItem extends BaseDocument {

    private String payrollId;
    private String employeeId;
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private String designationTitle;

    private int month;
    private int year;

    // Working Days
    private int totalWorkingDays;
    private double presentDays;
    private double leaveDays;
    private double unpaidLeaveDays;
    private double overtimeHours;

    // Earnings
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal specialAllowance;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal reimbursements;
    private BigDecimal grossSalary;

    // Deductions
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;
    private BigDecimal professionalTax;
    private BigDecimal incomeTaxTds;
    private BigDecimal loanEmiDeduction;
    private BigDecimal unpaidLeaveDeduction;
    private BigDecimal lateDeduction;
    private BigDecimal totalDeductions;

    // Net Salary
    private BigDecimal netSalary;

    private TaxRegime taxRegime;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String payslipPdfUrl;
    private boolean emailSent;
}
