package com.payroll.entity;

import com.payroll.common.BaseDocument;
import com.payroll.common.LeaveStatus;
import com.payroll.common.LeaveTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_requests")
public class LeaveRequest extends BaseDocument {

    private String employeeId;
    private String employeeCode;
    private String employeeName;

    private LeaveTypeEnum leaveType;

    private LocalDate startDate;
    private LocalDate endDate;
    private double totalDays;

    private String reason;
    private LeaveStatus status; // PENDING, APPROVED, REJECTED, CANCELLED

    private String approvedById;
    private String approvedByName;
    private String rejectionReason;

    private String documentUrl;
}
