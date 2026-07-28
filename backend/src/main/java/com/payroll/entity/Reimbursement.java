package com.payroll.entity;

import com.payroll.common.BaseDocument;
import com.payroll.common.ReimbursementStatus;
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
@Document(collection = "reimbursements")
public class Reimbursement extends BaseDocument {

    private String employeeId;
    private String employeeCode;
    private String employeeName;

    private String category; // MEDICAL, TRAVEL, INTERNET, FOOD, MISCELLANEOUS
    private BigDecimal amount;
    private LocalDate claimDate;
    private String description;

    private String receiptAttachmentUrl;

    private ReimbursementStatus status; // PENDING, APPROVED, REJECTED, PAID

    private String approvedById;
    private String approvedByName;
    private String rejectionReason;
}
