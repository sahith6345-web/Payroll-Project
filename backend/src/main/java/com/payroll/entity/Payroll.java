package com.payroll.entity;

import com.payroll.common.BaseDocument;
import com.payroll.common.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payrolls")
@CompoundIndex(def = "{'month': 1, 'year': 1}", unique = true)
public class Payroll extends BaseDocument {

    private int month; // 1 to 12
    private int year;  // e.g. 2026
    private String monthYear; // e.g. "2026-07"

    private PayrollStatus status; // DRAFT, PENDING_APPROVAL, APPROVED, LOCKED, DISBURSED

    private int totalEmployeesProcessed;

    private BigDecimal totalGrossSalary;
    private BigDecimal totalDeductions;
    private BigDecimal totalNetSalary;

    private LocalDate processingDate;
    private LocalDate paymentDate;

    private String approvedById;
    private String approvedByName;
    private String remarks;
}
