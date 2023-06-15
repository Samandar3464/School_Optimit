package com.example.repository;

import com.example.entity.Salary;
import com.example.enums.Months;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary,Integer> {

    Salary findByIdAndMonthAndActiveTrue(Integer id, Months months);
}
