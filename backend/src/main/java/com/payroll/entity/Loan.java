package com.payroll.entity;

import com.payroll.common.BaseDocument;
import com.payroll.common.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "loans")
public class Loan extends BaseDocument {

    private String employeeId;
    private String employeeCode;
    private String employeeName;

    private BigDecimal loanAmount;
    private BigDecimal monthlyEmi;
    private BigDecimal interestRatePercentage; // e.g. 5.0%

    private int totalTenureMonths;
    private int remainingTenureMonths;

    private BigDecimal totalPaidAmount;
    private BigDecimal remainingBalance;

    private LocalDate startDate;
    private LoanStatus status; // PENDING, APPROVED, REJECTED, ACTIVE, CLOSED

    private String reason;
    private String approvedById;
    private String approvedByName;
}
