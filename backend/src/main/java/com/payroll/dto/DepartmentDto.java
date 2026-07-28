package com.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DepartmentDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Department code is required")
        private String code;

        @NotBlank(message = "Department name is required")
        private String name;

        private String description;
        private String managerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String id;
        private String code;
        private String name;
        private String description;
        private String managerId;
        private String managerName;
        private int employeeCount;
    }
}
