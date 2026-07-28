package com.payroll.service;

import com.payroll.dto.DepartmentDto;
import com.payroll.entity.Department;
import com.payroll.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentDto.Response> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DepartmentDto.Response create(DepartmentDto.Request request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Department code already exists: " + request.getCode());
        }

        Department dept = Department.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .employeeCount(0)
                .build();

        return mapToResponse(departmentRepository.save(dept));
    }

    private DepartmentDto.Response mapToResponse(Department dept) {
        return DepartmentDto.Response.builder()
                .id(dept.getId())
                .code(dept.getCode())
                .name(dept.getName())
                .description(dept.getDescription())
                .managerId(dept.getManagerId())
                .employeeCount(dept.getEmployeeCount())
                .build();
    }
}
