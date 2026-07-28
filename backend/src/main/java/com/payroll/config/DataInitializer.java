package com.payroll.config;

import com.payroll.common.RoleType;
import com.payroll.entity.*;
import com.payroll.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final ShiftRepository shiftRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final PayrollRepository payrollRepository;
    private final PayrollItemRepository payrollItemRepository;
    private final HolidayRepository holidayRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("=== Data Initialization Starting ===");

        // ──────────────────────────────────────────────────
        // STEP 1: Clean up ALL old employee + salary data
        // to eliminate duplicates from prior runs.
        // ──────────────────────────────────────────────────
        long existingCount = employeeRepository.count();
        if (existingCount > 0) {
            log.info("Clearing {} existing employee records to eliminate duplicates...", existingCount);
            // Delete all salary structures
            salaryStructureRepository.deleteAll();
            // Delete all employees
            employeeRepository.deleteAll();
            log.info("Cleared all employees and salary structures.");
        }

        // ──────────────────────────────────────────────────
        // STEP 2: Seed Departments (idempotent)
        // ──────────────────────────────────────────────────
        Department eng, hr, fin, mkt;
        if (departmentRepository.count() == 0) {
            eng = departmentRepository.save(Department.builder().code("ENG").name("Engineering").description("Software engineering and DevOps").active(true).build());
            hr  = departmentRepository.save(Department.builder().code("HR").name("Human Resources").description("Talent acquisition and employee relations").active(true).build());
            fin = departmentRepository.save(Department.builder().code("FIN").name("Finance & Payroll").description("Payroll management, tax compliance and budgeting").active(true).build());
            mkt = departmentRepository.save(Department.builder().code("MKT").name("Marketing & Sales").description("Growth, branding and sales execution").active(true).build());
            log.info("Seeded 4 default departments.");
        } else {
            List<Department> depts = departmentRepository.findAll();
            eng = depts.stream().filter(d -> "ENG".equals(d.getCode())).findFirst().orElse(depts.get(0));
            hr  = depts.stream().filter(d -> "HR".equals(d.getCode())).findFirst().orElse(depts.size() > 1 ? depts.get(1) : depts.get(0));
            fin = depts.stream().filter(d -> "FIN".equals(d.getCode())).findFirst().orElse(depts.size() > 2 ? depts.get(2) : depts.get(0));
            mkt = depts.stream().filter(d -> "MKT".equals(d.getCode())).findFirst().orElse(depts.size() > 3 ? depts.get(3) : depts.get(0));
        }

        // ──────────────────────────────────────────────────
        // STEP 3: Seed Designations (idempotent)
        // ──────────────────────────────────────────────────
        if (designationRepository.count() == 0) {
            designationRepository.save(Designation.builder().title("Senior Software Engineer").code("SE-SR").departmentId(eng.getId()).baseSalaryMin(new BigDecimal("70000")).baseSalaryMax(new BigDecimal("120000")).active(true).build());
            designationRepository.save(Designation.builder().title("HR Manager").code("HR-MGR").departmentId(hr.getId()).baseSalaryMin(new BigDecimal("60000")).baseSalaryMax(new BigDecimal("95000")).active(true).build());
            designationRepository.save(Designation.builder().title("Payroll Specialist").code("FIN-SPEC").departmentId(fin.getId()).baseSalaryMin(new BigDecimal("55000")).baseSalaryMax(new BigDecimal("85000")).active(true).build());
            log.info("Seeded default designations.");
        }

        // ──────────────────────────────────────────────────
        // STEP 4: Seed Shift (idempotent)
        // ──────────────────────────────────────────────────
        Shift morningShift;
        if (shiftRepository.count() == 0) {
            morningShift = shiftRepository.save(Shift.builder()
                    .name("Regular Morning Shift")
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(18, 0))
                    .breakDurationMinutes(60)
                    .gracePeriodMinutes(15)
                    .active(true)
                    .build());
        } else {
            morningShift = shiftRepository.findAll().get(0);
        }

        // ──────────────────────────────────────────────────
        // STEP 5: Seed Super Admin user (idempotent)
        // ──────────────────────────────────────────────────
        User superAdmin;
        Optional<User> existingSuperAdmin = userRepository.findByEmail("superadmin@payroll.com");
        if (existingSuperAdmin.isEmpty()) {
            superAdmin = userRepository.save(User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("superadmin@payroll.com")
                    .username("superadmin")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .roles(Set.of(RoleType.SUPER_ADMIN, RoleType.ADMIN))
                    .enabled(true)
                    .emailVerified(true)
                    .build());
        } else {
            superAdmin = existingSuperAdmin.get();
            // Ensure roles are correct
            superAdmin.setRoles(Set.of(RoleType.SUPER_ADMIN, RoleType.ADMIN));
            superAdmin = userRepository.save(superAdmin);
        }

        // Create SuperAdmin employee record
        Employee adminEmp = employeeRepository.save(Employee.builder()
                .userId(superAdmin.getId())
                .employeeCode("EMP-0001")
                .firstName("Super")
                .lastName("Admin")
                .email("superadmin@payroll.com")
                .phone("+1 555-0100")
                .dateOfJoining(LocalDate.of(2023, 1, 1))
                .departmentId(eng.getId())
                .departmentName("Engineering")
                .designationTitle("Chief Technology Officer")
                .shiftId(morningShift.getId())
                .status("ACTIVE")
                .build());
        superAdmin.setEmployeeId(adminEmp.getId());
        userRepository.save(superAdmin);
        createSalaryStructure(adminEmp, new BigDecimal("12000"));

        // ──────────────────────────────────────────────────
        // STEP 6: Seed HR User (idempotent)
        // ──────────────────────────────────────────────────
        User hrUser;
        Optional<User> existingHr = userRepository.findByEmail("hr@payroll.com");
        if (existingHr.isEmpty()) {
            hrUser = userRepository.save(User.builder()
                    .firstName("Sarah")
                    .lastName("Jenkins")
                    .email("hr@payroll.com")
                    .username("hruser")
                    .password(passwordEncoder.encode("Hr@12345"))
                    .roles(Set.of(RoleType.HR))
                    .enabled(true)
                    .emailVerified(true)
                    .build());
        } else {
            hrUser = existingHr.get();
        }

        Employee hrEmp = employeeRepository.save(Employee.builder()
                .userId(hrUser.getId())
                .employeeCode("EMP-1002")
                .firstName("Sarah")
                .lastName("Jenkins")
                .email("hr@payroll.com")
                .phone("+1 555-0102")
                .dateOfJoining(LocalDate.of(2023, 3, 15))
                .departmentId(hr.getId())
                .departmentName("Human Resources")
                .designationTitle("HR Manager")
                .shiftId(morningShift.getId())
                .status("ACTIVE")
                .build());
        hrUser.setEmployeeId(hrEmp.getId());
        userRepository.save(hrUser);
        createSalaryStructure(hrEmp, new BigDecimal("7200"));

        // ──────────────────────────────────────────────────
        // STEP 7: Seed Employee User (idempotent)
        // ──────────────────────────────────────────────────
        User devUser;
        Optional<User> existingDev = userRepository.findByEmail("employee@payroll.com");
        if (existingDev.isEmpty()) {
            devUser = userRepository.save(User.builder()
                    .firstName("Alex")
                    .lastName("Rivers")
                    .email("employee@payroll.com")
                    .username("alexdev")
                    .password(passwordEncoder.encode("Emp@12345"))
                    .roles(Set.of(RoleType.EMPLOYEE))
                    .enabled(true)
                    .emailVerified(true)
                    .build());
        } else {
            devUser = existingDev.get();
        }

        Employee devEmp = employeeRepository.save(Employee.builder()
                .userId(devUser.getId())
                .employeeCode("EMP-1003")
                .firstName("Alex")
                .lastName("Rivers")
                .email("employee@payroll.com")
                .phone("+1 555-0103")
                .dateOfJoining(LocalDate.of(2023, 6, 1))
                .departmentId(eng.getId())
                .departmentName("Engineering")
                .designationTitle("Senior Fullstack Developer")
                .shiftId(morningShift.getId())
                .status("ACTIVE")
                .build());
        devUser.setEmployeeId(devEmp.getId());
        userRepository.save(devUser);
        createSalaryStructure(devEmp, new BigDecimal("6800"));

        // ──────────────────────────────────────────────────
        // STEP 8: Seed 7 more distinct employees (no users)
        // ──────────────────────────────────────────────────
        String[][] additionalProfiles = {
            {"Michael", "Scott",     "michael.scott@payroll.com",     "+1 555-0104", "EMP-1004", "Operations Manager",         "9000"},
            {"Pam",     "Beesly",    "pam.beesly@payroll.com",        "+1 555-0105", "EMP-1005", "HR Coordinator",             "5500"},
            {"Jim",     "Halpert",   "jim.halpert@payroll.com",       "+1 555-0106", "EMP-1006", "Sales Director",             "7500"},
            {"Dwight",  "Schrute",   "dwight.schrute@payroll.com",    "+1 555-0107", "EMP-1007", "Marketing Specialist",       "7800"},
            {"Angela",  "Martin",    "angela.martin@payroll.com",     "+1 555-0108", "EMP-1008", "Lead Accountant",            "7000"},
            {"Oscar",   "Martinez",  "oscar.martinez@payroll.com",    "+1 555-0109", "EMP-1009", "Senior Financial Analyst",   "6500"},
            {"Kevin",   "Malone",    "kevin.malone@payroll.com",      "+1 555-0110", "EMP-1010", "Payroll Coordinator",        "5800"}
        };

        // Map departments for each additional employee
        Department[] deptMapping = { hr, hr, mkt, mkt, fin, fin, fin };

        for (int i = 0; i < additionalProfiles.length; i++) {
            String[] p = additionalProfiles[i];
            Department dept = deptMapping[i];

            Employee emp = employeeRepository.save(Employee.builder()
                    .employeeCode(p[4])
                    .firstName(p[0])
                    .lastName(p[1])
                    .email(p[2])
                    .phone(p[3])
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .designationTitle(p[5])
                    .shiftId(morningShift.getId())
                    .dateOfJoining(LocalDate.of(2024, 1, 15).plusMonths(i))
                    .status("ACTIVE")
                    .build());

            createSalaryStructure(emp, new BigDecimal(p[6]));
        }

        // ──────────────────────────────────────────────────
        // STEP 9: Seed Holidays (idempotent)
        // ──────────────────────────────────────────────────
        if (holidayRepository.count() == 0) {
            holidayRepository.saveAll(List.of(
                    Holiday.builder().title("New Year's Day").date(LocalDate.of(2026, 1, 1)).type("NATIONAL").description("Global holiday").build(),
                    Holiday.builder().title("Labor Day").date(LocalDate.of(2026, 5, 1)).type("NATIONAL").description("Labor day celebration").build(),
                    Holiday.builder().title("Independence Day").date(LocalDate.of(2026, 7, 4)).type("NATIONAL").description("National independence day").build(),
                    Holiday.builder().title("Christmas Day").date(LocalDate.of(2026, 12, 25)).type("NATIONAL").description("Christmas festival").build()
            ));
            log.info("Seeded 4 default holidays.");
        }

        log.info("=== Data Initialization Complete. {} employees in database. ===", employeeRepository.count());
    }

    private void createSalaryStructure(Employee emp, BigDecimal basicSalary) {
        salaryStructureRepository.save(SalaryStructure.builder()
                .employeeId(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .basicSalary(basicSalary)
                .houseRentAllowance(basicSalary.multiply(new BigDecimal("0.40")))
                .dearnessAllowance(basicSalary.multiply(new BigDecimal("0.10")))
                .specialAllowance(new BigDecimal("1000.00"))
                .medicalAllowance(new BigDecimal("250.00"))
                .pfPercentage(new BigDecimal("12.00"))
                .esiPercentage(new BigDecimal("0.75"))
                .professionalTax(new BigDecimal("200.00"))
                .effectiveFrom(LocalDate.of(2024, 1, 1))
                .active(true)
                .build());
    }
}
