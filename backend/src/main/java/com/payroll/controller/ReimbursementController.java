package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.common.ReimbursementStatus;
import com.payroll.entity.Reimbursement;
import com.payroll.repository.ReimbursementRepository;
import com.payroll.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reimbursements")
@RequiredArgsConstructor
public class ReimbursementController {

    private final ReimbursementRepository reimbursementRepository;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Reimbursement>> submitClaim(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ClaimPayload payload) {

        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();

        Reimbursement claim = Reimbursement.builder()
                .employeeId(empId)
                .category(payload.getExpenseType())
                .description(payload.getDescription())
                .amount(payload.getAmount())
                .claimDate(payload.getExpenseDate() != null ? payload.getExpenseDate() : LocalDate.now())
                .receiptAttachmentUrl(payload.getReceiptUrl())
                .status(ReimbursementStatus.PENDING)
                .build();

        Reimbursement saved = reimbursementRepository.save(claim);
        return ResponseEntity.ok(ApiResponse.success(saved, "Reimbursement claim submitted successfully"));
    }

    @GetMapping("/my-claims")
    public ResponseEntity<ApiResponse<List<Reimbursement>>> getMyClaims(@AuthenticationPrincipal UserPrincipal currentUser) {
        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        List<Reimbursement> list = reimbursementRepository.findByEmployeeId(empId);
        return ResponseEntity.ok(ApiResponse.success(list, "Fetched user reimbursement claims"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'PAYROLL_MANAGER', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Reimbursement>>> getAllClaims() {
        return ResponseEntity.ok(ApiResponse.success(reimbursementRepository.findAll(), "Fetched all reimbursement claims"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'PAYROLL_MANAGER')")
    public ResponseEntity<ApiResponse<Reimbursement>> updateStatus(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody StatusPayload payload) {

        Reimbursement claim = reimbursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + id));

        claim.setStatus(payload.getStatus());
        claim.setApprovedById(currentUser.getId());
        claim.setApprovedByName(currentUser.getFirstName() + " " + currentUser.getLastName());
        if (payload.getStatus() == ReimbursementStatus.REJECTED && payload.getComments() != null) {
            claim.setRejectionReason(payload.getComments());
        }

        Reimbursement updated = reimbursementRepository.save(claim);
        return ResponseEntity.ok(ApiResponse.success(updated, "Claim status updated to " + payload.getStatus()));
    }

    @Data
    public static class ClaimPayload {
        private String title;
        private String description;
        private String expenseType;
        private BigDecimal amount;
        private LocalDate expenseDate;
        private String receiptUrl;
    }

    @Data
    public static class StatusPayload {
        private ReimbursementStatus status;
        private String comments;
    }
}
