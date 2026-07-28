package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.dto.DepartmentDto;
import com.payroll.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(departmentService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    public ResponseEntity<ApiResponse<DepartmentDto.Response>> create(@Valid @RequestBody DepartmentDto.Request request) {
        return ResponseEntity.ok(ApiResponse.success(departmentService.create(request), "Department created successfully"));
    }
}
