package com.payroll;

import com.payroll.common.RoleType;
import com.payroll.entity.Department;
import com.payroll.entity.Employee;
import com.payroll.entity.User;
import com.payroll.repository.DepartmentRepository;
import com.payroll.repository.EmployeeRepository;
import com.payroll.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@RequiredArgsConstructor
public class PayrollBackendApplication {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(PayrollBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Seed Super Admin User if empty
            if (!userRepository.existsByEmail("admin@enterprise-payroll.com")) {
                User admin = User.builder()
                        .firstName("Super")
                        .lastName("Admin")
                        .email("admin@enterprise-payroll.com")
                        .username("admin")
                        .password(passwordEncoder.encode("Admin@123"))
                        .roles(Set.of(RoleType.SUPER_ADMIN, RoleType.ADMIN, RoleType.PAYROLL_MANAGER))
                        .enabled(true)
                        .emailVerified(true)
                        .build();

                User savedUser = userRepository.save(admin);

                // Seed default Department
                Department dept = Department.builder()
                        .code("ENG")
                        .name("Software Engineering")
                        .description("Core product development & technology")
                        .employeeCount(1)
                        .build();
                Department savedDept = departmentRepository.save(dept);

                // Seed Admin Employee
                Employee emp = Employee.builder()
                        .userId(savedUser.getId())
                        .employeeCode("EMP-1001")
                        .firstName("Super")
                        .lastName("Admin")
                        .email("admin@enterprise-payroll.com")
                        .departmentId(savedDept.getId())
                        .departmentName(savedDept.getName())
                        .designationTitle("Chief Technology Officer")
                        .status("ACTIVE")
                        .dateOfJoining(LocalDate.now().minusYears(3))
                        .build();

                Employee savedEmp = employeeRepository.save(emp);
                savedUser.setEmployeeId(savedEmp.getId());
                userRepository.save(savedUser);

                System.out.println(">>> Initialized Super Admin user: admin@enterprise-payroll.com / Admin@123");
            }
        };
    }
}
