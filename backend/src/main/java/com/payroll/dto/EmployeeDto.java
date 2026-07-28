package com.payroll.dto;

import com.payroll.entity.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class EmployeeDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        private String phone;
        private LocalDate dateOfBirth;
        private LocalDate dateOfJoining;
        private String gender;
        private String maritalStatus;

        private String departmentId;
        private String designationId;
        private String managerId;
        private String shiftId;
        private String employmentType;

        private Employee.Address currentAddress;
        private Employee.Address permanentAddress;
        private Employee.BankDetails bankDetails;
        private List<Employee.EmergencyContact> emergencyContacts;
        private List<Employee.Education> educationHistory;
        private List<Employee.Experience> experienceHistory;
        private List<String> skills;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeResponse {
        private String id;
        private String employeeCode;
        private String userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDate dateOfBirth;
        private LocalDate dateOfJoining;
        private String gender;
        private String maritalStatus;
        private String departmentId;
        private String departmentName;
        private String designationId;
        private String designationTitle;
        private String managerId;
        private String managerName;
        private String shiftId;
        private String employmentType;
        private String status;
        private Employee.Address currentAddress;
        private Employee.Address permanentAddress;
        private Employee.BankDetails bankDetails;
        private List<Employee.EmergencyContact> emergencyContacts;
        private List<Employee.Education> educationHistory;
        private List<Employee.Experience> experienceHistory;
        private List<String> skills;
        private String profilePictureUrl;
    }
}
