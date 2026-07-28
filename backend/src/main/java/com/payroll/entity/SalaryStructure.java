package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "salary_structures")
public class SalaryStructure extends BaseDocument {

    @Indexed(unique = true)
    private String employeeId;
    private String employeeCode;

    private BigDecimal basicSalary;
    private BigDecimal houseRentAllowance; // HRA
    private BigDecimal dearnessAllowance; // DA
    private BigDecimal specialAllowance;
    private BigDecimal medicalAllowance;
    private BigDecimal transportAllowance;

    // Fixed Percentage Rules
    private BigDecimal pfPercentage; // Provident Fund e.g. 12%
    private BigDecimal esiPercentage; // ESI e.g. 0.75%
    private BigDecimal professionalTax; // Fixed PT per month e.g. 200

    private BigDecimal ctc; // Cost to Company (Annual)
    private BigDecimal grossMonthly;
    private BigDecimal netMonthlyEstimate;

    private String currency = "USD";

    private LocalDate effectiveFrom;

    @Builder.Default
    private boolean active = true;
}
