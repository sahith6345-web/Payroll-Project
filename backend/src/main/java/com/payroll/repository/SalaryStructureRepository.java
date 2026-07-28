package com.payroll.repository;

import com.payroll.entity.SalaryStructure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends MongoRepository<SalaryStructure, String> {
    Optional<SalaryStructure> findByEmployeeId(String employeeId);
}
