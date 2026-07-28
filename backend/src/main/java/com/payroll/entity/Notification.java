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
@Document(collection = "notifications")
public class Notification extends BaseDocument {

    private String userId;
    private String title;
    private String message;
    private String type; // PAYROLL, LEAVE, ATTENDANCE, ANNOUNCEMENT, SYSTEM
    private String link;
    private boolean isRead = false;
    private LocalDateTime timestamp = LocalDateTime.now();
}
