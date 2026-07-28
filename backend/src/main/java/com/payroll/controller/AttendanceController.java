package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.common.AttendanceStatus;
import com.payroll.entity.Attendance;
import com.payroll.repository.AttendanceRepository;
import com.payroll.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;

    @PostMapping("/clock-in")
    public ResponseEntity<ApiResponse<Attendance>> clockIn(@AuthenticationPrincipal UserPrincipal currentUser) {
        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(empId, today)
                .orElse(Attendance.builder()
                        .employeeId(empId)
                        .date(today)
                        .status(AttendanceStatus.PRESENT)
                        .build());

        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);
        Attendance saved = attendanceRepository.save(attendance);

        return ResponseEntity.ok(ApiResponse.success(saved, "Clocked in successfully at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }

    @PostMapping("/clock-out")
    public ResponseEntity<ApiResponse<Attendance>> clockOut(@AuthenticationPrincipal UserPrincipal currentUser) {
        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(empId, today)
                .orElseThrow(() -> new RuntimeException("No clock-in record found for today"));

        attendance.setCheckOutTime(LocalDateTime.now());
        if (attendance.getCheckInTime() != null) {
            long minutes = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
            attendance.setTotalHours(Math.max(0.0, (minutes - 60) / 60.0)); // Subtracting 1hr break
        }

        Attendance saved = attendanceRepository.save(attendance);
        return ResponseEntity.ok(ApiResponse.success(saved, "Clocked out successfully at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }

    @GetMapping("/my-records")
    public ResponseEntity<ApiResponse<List<Attendance>>> getMyAttendance(@AuthenticationPrincipal UserPrincipal currentUser) {
        String empId = currentUser.getEmployeeId() != null ? currentUser.getEmployeeId() : currentUser.getId();
        List<Attendance> list = attendanceRepository.findByEmployeeId(empId);
        return ResponseEntity.ok(ApiResponse.success(list, "Attendance records fetched"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'PAYROLL_MANAGER', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAllAttendance(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String date) {
        if (employeeId != null) {
            return ResponseEntity.ok(ApiResponse.success(attendanceRepository.findByEmployeeId(employeeId), "Fetched by employee"));
        }
        return ResponseEntity.ok(ApiResponse.success(attendanceRepository.findAll(), "Fetched all attendance records"));
    }
}
