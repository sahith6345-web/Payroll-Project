package com.payroll.entity;

import com.payroll.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee extends BaseDocument {

    @Indexed(unique = true)
    private String employeeCode; // e.g. EMP-1001

    private String userId; // Reference to User entity

    private String firstName;
    private String lastName;
    
    @Indexed(unique = true)
    private String email;

    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;

    private String gender; // MALE, FEMALE, OTHER
    private String maritalStatus; // SINGLE, MARRIED, DIVORCED

    private String departmentId;
    private String departmentName;

    private String designationId;
    private String designationTitle;

    private String managerId;
    private String managerName;

    private String shiftId;
    
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERN
    private String status; // ACTIVE, ON_LEAVE, TERMINATED, RESIGNED

    // Sub-documents
    @Builder.Default
    private Address currentAddress = new Address();

    @Builder.Default
    private Address permanentAddress = new Address();

    @Builder.Default
    private BankDetails bankDetails = new BankDetails();

    @Builder.Default
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    @Builder.Default
    private List<Education> educationHistory = new ArrayList<>();

    @Builder.Default
    private List<Experience> experienceHistory = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private String profilePictureUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String country;
        private String zipCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BankDetails {
        private String bankName;
        private String accountNumber;
        private String ifscCode;
        private String branchName;
        private String panNumber;
        private String pfNumber;
        private String uanNumber;
        private String esiNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmergencyContact {
        private String name;
        private String relationship;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Education {
        private String degree;
        private String institution;
        private Integer completionYear;
        private String grade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Experience {
        private String companyName;
        private String designation;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
