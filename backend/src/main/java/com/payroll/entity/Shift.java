package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shifts")
public class Shift extends BaseDocument {

    private String name; // DAY_SHIFT, NIGHT_SHIFT, ROTATING
    private String code;
    
    private LocalTime startTime; // e.g. 09:00
    private LocalTime endTime;   // e.g. 17:00
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;

    @Builder.Default
    private int breakDurationMinutes = 60;

    @Builder.Default
    private int gracePeriodMinutes = 15;

    @Builder.Default
    private double fullWorkHours = 8.0;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean isDefault = false;
}
