package com.example.repository;

import com.example.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary,Integer> {

    List<Salary> findAllByUserIdAndActiveTrue(Integer userId);

    Optional<Salary> findByUserIdAndActiveTrue(Integer id);
}
