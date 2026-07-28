package com.payroll.service;

import com.payroll.common.PagedResponse;
import com.payroll.dto.EmployeeDto;
import com.payroll.entity.Department;
import com.payroll.entity.Designation;
import com.payroll.entity.Employee;
import com.payroll.entity.SalaryStructure;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.DesignationRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;

    public PagedResponse<EmployeeDto.EmployeeResponse> getAllEmployees(int page, int size, String search) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Employee> employeePage;

        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.searchEmployees(search.trim(), pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }

        List<EmployeeDto.EmployeeResponse> content = employeePage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<EmployeeDto.EmployeeResponse>builder()
                .content(content)
                .page(employeePage.getNumber())
                .size(employeePage.getSize())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .last(employeePage.isLast())
                .build();
    }

    public EmployeeDto.EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    public EmployeeDto.EmployeeResponse createEmployee(EmployeeDto.EmployeeRequest request) {
        // Resolve department name from departmentId
        String departmentName = null;
        if (request.getDepartmentId() != null && !request.getDepartmentId().isBlank()) {
            departmentName = departmentRepository.findById(request.getDepartmentId())
                    .map(Department::getName)
                    .orElse(null);
        }

        // Resolve designation title from designationId
        String designationTitle = null;
        if (request.getDesignationId() != null && !request.getDesignationId().isBlank()) {
            designationTitle = designationRepository.findById(request.getDesignationId())
                    .map(Designation::getTitle)
                    .orElse(null);
        }

        Employee employee = Employee.builder()
                .employeeCode("EMP-" + (1000 + (int)(Math.random() * 9000)))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .dateOfJoining(request.getDateOfJoining() != null ? request.getDateOfJoining() : LocalDate.now())
                .gender(request.getGender())
                .maritalStatus(request.getMaritalStatus())
                .departmentId(request.getDepartmentId())
                .departmentName(departmentName)
                .designationId(request.getDesignationId())
                .designationTitle(designationTitle)
                .managerId(request.getManagerId())
                .shiftId(request.getShiftId())
                .employmentType(request.getEmploymentType() != null ? request.getEmploymentType() : "FULL_TIME")
                .status("ACTIVE")
                .currentAddress(request.getCurrentAddress())
                .permanentAddress(request.getPermanentAddress())
                .bankDetails(request.getBankDetails())
                .emergencyContacts(request.getEmergencyContacts())
                .educationHistory(request.getEducationHistory())
                .experienceHistory(request.getExperienceHistory())
                .skills(request.getSkills())
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Created employee: {} {} ({})", saved.getFirstName(), saved.getLastName(), saved.getEmployeeCode());

        // Auto-create a default salary structure
        if (salaryStructureRepository.findByEmployeeId(saved.getId()).isEmpty()) {
            BigDecimal basicSalary = BigDecimal.valueOf(6000.00);
            salaryStructureRepository.save(SalaryStructure.builder()
                    .employeeId(saved.getId())
                    .employeeCode(saved.getEmployeeCode())
                    .basicSalary(basicSalary)
                    .houseRentAllowance(basicSalary.multiply(BigDecimal.valueOf(0.40)))
                    .dearnessAllowance(basicSalary.multiply(BigDecimal.valueOf(0.10)))
                    .specialAllowance(BigDecimal.valueOf(1000.00))
                    .medicalAllowance(BigDecimal.valueOf(250.00))
                    .pfPercentage(BigDecimal.valueOf(12.00))
                    .esiPercentage(BigDecimal.valueOf(0.75))
                    .professionalTax(BigDecimal.valueOf(200.00))
                    .effectiveFrom(LocalDate.now())
                    .active(true)
                    .build());
        }

        return mapToResponse(saved);
    }

    public EmployeeDto.EmployeeResponse updateEmployee(String id, EmployeeDto.EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartmentId(request.getDepartmentId());
        employee.setDesignationId(request.getDesignationId());
        employee.setEmploymentType(request.getEmploymentType());

        // Resolve names
        if (request.getDepartmentId() != null && !request.getDepartmentId().isBlank()) {
            departmentRepository.findById(request.getDepartmentId())
                    .ifPresent(d -> employee.setDepartmentName(d.getName()));
        }
        if (request.getDesignationId() != null && !request.getDesignationId().isBlank()) {
            designationRepository.findById(request.getDesignationId())
                    .ifPresent(d -> employee.setDesignationTitle(d.getTitle()));
        }

        if (request.getBankDetails() != null) {
            employee.setBankDetails(request.getBankDetails());
        }

        Employee updated = employeeRepository.save(employee);
        return mapToResponse(updated);
    }

    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.setDeleted(true);
        employee.setStatus("TERMINATED");
        employeeRepository.save(employee);
    }

    private EmployeeDto.EmployeeResponse mapToResponse(Employee emp) {
        return EmployeeDto.EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .userId(emp.getUserId())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .dateOfBirth(emp.getDateOfBirth())
                .dateOfJoining(emp.getDateOfJoining())
                .gender(emp.getGender())
                .maritalStatus(emp.getMaritalStatus())
                .departmentId(emp.getDepartmentId())
                .departmentName(emp.getDepartmentName())
                .designationId(emp.getDesignationId())
                .designationTitle(emp.getDesignationTitle())
                .managerId(emp.getManagerId())
                .managerName(emp.getManagerName())
                .shiftId(emp.getShiftId())
                .employmentType(emp.getEmploymentType())
                .status(emp.getStatus())
                .currentAddress(emp.getCurrentAddress())
                .permanentAddress(emp.getPermanentAddress())
                .bankDetails(emp.getBankDetails())
                .emergencyContacts(emp.getEmergencyContacts())
                .educationHistory(emp.getEducationHistory())
                .experienceHistory(emp.getExperienceHistory())
                .skills(emp.getSkills())
                .profilePictureUrl(emp.getProfilePictureUrl())
                .build();
    }
}
