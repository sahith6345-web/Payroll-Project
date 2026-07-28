package com.payroll.repository;

import com.payroll.entity.Payroll;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRepository extends MongoRepository<Payroll, String> {
    Optional<Payroll> findByMonthAndYear(int month, int year);
    Optional<Payroll> findByMonthYear(String monthYear);
}
