package com.example.repository;

import com.example.entity.Salary;
import com.example.enums.Months;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary,Integer> {

    Optional<Salary> findByIdAndMonthAndActiveTrue(Integer id, Months months);
}
