package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.entity.SalaryStructure;
import com.payroll.repository.SalaryStructureRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureRepository salaryStructureRepository;

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<SalaryStructure>> getSalaryStructure(@PathVariable String employeeId) {
        SalaryStructure structure = salaryStructureRepository.findByEmployeeId(employeeId)
                .orElseGet(() -> SalaryStructure.builder()
                        .employeeId(employeeId)
                        .basicSalary(BigDecimal.valueOf(5000.00))
                        .houseRentAllowance(BigDecimal.valueOf(2000.00))
                        .dearnessAllowance(BigDecimal.valueOf(500.00))
                        .specialAllowance(BigDecimal.valueOf(1000.00))
                        .medicalAllowance(BigDecimal.valueOf(200.00))
                        .pfPercentage(BigDecimal.valueOf(12.00))
                        .esiPercentage(BigDecimal.valueOf(0.75))
                        .professionalTax(BigDecimal.valueOf(200.00))
                        .effectiveFrom(LocalDate.now())
                        .active(true)
                        .build());
        return ResponseEntity.ok(ApiResponse.success(structure, "Salary structure fetched successfully"));
    }

    @PostMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<SalaryStructure>> updateSalaryStructure(
            @PathVariable String employeeId,
            @RequestBody SalaryStructurePayload payload
    ) {
        SalaryStructure structure = salaryStructureRepository.findByEmployeeId(employeeId)
                .orElseGet(() -> SalaryStructure.builder()
                        .employeeId(employeeId)
                        .build());

        if (payload.getBasicSalary() != null) structure.setBasicSalary(payload.getBasicSalary());
        if (payload.getHouseRentAllowance() != null) structure.setHouseRentAllowance(payload.getHouseRentAllowance());
        if (payload.getDearnessAllowance() != null) structure.setDearnessAllowance(payload.getDearnessAllowance());
        if (payload.getSpecialAllowance() != null) structure.setSpecialAllowance(payload.getSpecialAllowance());
        if (payload.getMedicalAllowance() != null) structure.setMedicalAllowance(payload.getMedicalAllowance());
        if (payload.getPfPercentage() != null) structure.setPfPercentage(payload.getPfPercentage());
        if (payload.getEsiPercentage() != null) structure.setEsiPercentage(payload.getEsiPercentage());
        if (payload.getProfessionalTax() != null) structure.setProfessionalTax(payload.getProfessionalTax());
        if (payload.getEffectiveFrom() != null) structure.setEffectiveFrom(payload.getEffectiveFrom());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return ResponseEntity.ok(ApiResponse.success(saved, "Salary structure saved successfully"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalaryStructurePayload {
        private BigDecimal basicSalary;
        private BigDecimal houseRentAllowance;
        private BigDecimal dearnessAllowance;
        private BigDecimal specialAllowance;
        private BigDecimal medicalAllowance;
        private BigDecimal pfPercentage;
        private BigDecimal esiPercentage;
        private BigDecimal professionalTax;
        private LocalDate effectiveFrom;
    }
}
