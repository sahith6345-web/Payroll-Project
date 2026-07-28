package com.payroll.controller;

import com.payroll.common.ApiResponse;
import com.payroll.common.PagedResponse;
import com.payroll.dto.EmployeeDto;
import com.payroll.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeDto.EmployeeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees(page, size, search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto.EmployeeResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDto.EmployeeResponse>> create(@Valid @RequestBody EmployeeDto.EmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.createEmployee(request), "Employee created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDto.EmployeeResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody EmployeeDto.EmployeeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.updateEmployee(id, request), "Employee updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee deleted successfully"));
    }
}
