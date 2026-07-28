package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog extends BaseDocument {

    private String userId;
    private String userEmail;
    private String action; // CREATE, UPDATE, DELETE, LOGIN, APPROVE, GENERATE_PAYROLL
    private String entityName;
    private String entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp = LocalDateTime.now();
}
