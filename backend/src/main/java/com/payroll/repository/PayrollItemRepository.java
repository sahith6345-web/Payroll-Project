package com.payroll.repository;

import com.payroll.entity.PayrollItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollItemRepository extends MongoRepository<PayrollItem, String> {
    List<PayrollItem> findByPayrollId(String payrollId);
    Optional<PayrollItem> findByPayrollIdAndEmployeeId(String payrollId, String employeeId);
    List<PayrollItem> findByEmployeeId(String employeeId);
}
