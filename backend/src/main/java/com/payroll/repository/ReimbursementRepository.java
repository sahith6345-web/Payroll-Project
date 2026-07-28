package com.payroll.repository;

import com.payroll.common.ReimbursementStatus;
import com.payroll.entity.Reimbursement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReimbursementRepository extends MongoRepository<Reimbursement, String> {
    List<Reimbursement> findByEmployeeId(String employeeId);
    List<Reimbursement> findByStatus(ReimbursementStatus status);
}
