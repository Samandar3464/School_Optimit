package com.example.repository;

import com.example.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Month;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary,Integer> {

//    Optional<SalaryNew> findByUserIdAndActiveTrueAndSalaryDateBetween(Integer userId, LocalDate salaryDate, LocalDate salaryDate2);

    Optional<Salary> findByUserIdAndMonthsAndYearAndActiveTrue(Integer userId, Month months, Integer year);
}
