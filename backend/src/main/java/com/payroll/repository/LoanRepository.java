package com.payroll.repository;

import com.payroll.common.LoanStatus;
import com.payroll.entity.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByEmployeeId(String employeeId);
    List<Loan> findByEmployeeIdAndStatus(String employeeId, LoanStatus status);
}
