package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leave_balances")
@CompoundIndex(def = "{'employeeId': 1, 'year': 1}", unique = true)
public class LeaveBalance extends BaseDocument {

    private String employeeId;
    private int year;

    private double casualLeaveAllocated = 12.0;
    private double casualLeaveUsed = 0.0;

    private double sickLeaveAllocated = 12.0;
    private double sickLeaveUsed = 0.0;

    private double earnedLeaveAllocated = 15.0;
    private double earnedLeaveUsed = 0.0;

    private double maternityLeaveAllocated = 180.0;
    private double maternityLeaveUsed = 0.0;

    private double paternityLeaveAllocated = 15.0;
    private double paternityLeaveUsed = 0.0;
}
