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

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "designations")
public class Designation extends BaseDocument {

    @Indexed(unique = true)
    private String code;

    private String title;

    private String departmentId;
    private String departmentName;

    private String description;

    private String payGrade;

    @Builder.Default
    private BigDecimal baseSalaryMin = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal baseSalaryMax = BigDecimal.ZERO;

    @Builder.Default
    private boolean active = true;
}