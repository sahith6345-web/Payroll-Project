package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "departments")
public class Department extends BaseDocument {

    @Indexed(unique = true)
    private String code;

    @Indexed
    private String name;

    private String description;

    private String managerId;

    private String managerName;

    @Builder.Default
    private Integer employeeCount = 0;

    @Builder.Default
    private Boolean active = true;
}