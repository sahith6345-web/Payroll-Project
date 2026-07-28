package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.common.LeaveStatus;
import com.payroll.common.LeaveTypeEnum;
import com.payroll.entity.Holiday;
import com.payroll.entity.LeaveRequest;
import com.payroll.repository.HolidayRepository;
import com.payroll.repository.LeaveRequestRepository;
import com.payroll.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestRepository leaveRequestRepository;
    private final HolidayRepository holidayRepository;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveRequest>> applyLeave(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody LeaveRequestPayload payload) {

        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        long days = ChronoUnit.DAYS.between(payload.getStartDate(), payload.getEndDate()) + 1;

        LeaveRequest request = LeaveRequest.builder()
                .employeeId(empId)
                .leaveType(LeaveTypeEnum.valueOf(payload.getLeaveType()))
                .startDate(payload.getStartDate())
                .endDate(payload.getEndDate())
                .totalDays(days)
                .reason(payload.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(request);
        return ResponseEntity.ok(ApiResponse.success(saved, "Leave application submitted successfully"));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getMyLeaves(@AuthenticationPrincipal UserPrincipal currentUser) {
        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(empId);
        return ResponseEntity.ok(ApiResponse.success(list, "My leave requests fetched"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getAllLeaves() {
        return ResponseEntity.ok(ApiResponse.success(leaveRequestRepository.findAll(), "All leave requests fetched"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<ApiResponse<LeaveRequest>> updateStatus(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody StatusUpdatePayload payload) {

        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found: " + id));

        request.setStatus(payload.getStatus());
        request.setApprovedById(currentUser.getId());
        request.setApprovedByName(currentUser.getFirstName() + " " + currentUser.getLastName());
        if (payload.getStatus() == LeaveStatus.REJECTED && payload.getComments() != null) {
            request.setRejectionReason(payload.getComments());
        }

        LeaveRequest updated = leaveRequestRepository.save(request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave status updated to " + payload.getStatus()));
    }

    @GetMapping("/holidays")
    public ResponseEntity<ApiResponse<List<Holiday>>> getHolidays() {
        return ResponseEntity.ok(ApiResponse.success(holidayRepository.findAll(), "Holidays list fetched"));
    }

    @PostMapping("/holidays")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<Holiday>> addHoliday(@Valid @RequestBody Holiday holiday) {
        return ResponseEntity.ok(ApiResponse.success(holidayRepository.save(holiday), "Holiday added successfully"));
    }

    @Data
    public static class LeaveRequestPayload {
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
    }

    @Data
    public static class StatusUpdatePayload {
        private LeaveStatus status;
        private String comments;
    }
}
