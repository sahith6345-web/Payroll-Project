package com.payroll.entity;

import com.payroll.common.BaseDocument;
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
@Document(collection = "holidays")
public class Holiday extends BaseDocument {

    private String title;
    private LocalDate date;
    private String type; // NATIONAL, REGIONAL, OPTIONAL, COMPANY
    private String description;
    private boolean isMandatory = true;
}
