package com.payroll.entity;

import com.payroll.common.AttendanceStatus;
import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance")
@CompoundIndex(def = "{'employeeId': 1, 'date': 1}", unique = true)
public class Attendance extends BaseDocument {

    private String employeeId;
    private String employeeCode;
    private String employeeName;
    
    private LocalDate date;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private double totalHours;
    private double breakHours;
    private double overtimeHours;

    private AttendanceStatus status; // PRESENT, ABSENT, HALF_DAY, LATE, OVERTIME
    private String remarks;

    private String ipAddress;
    private String location;
}
