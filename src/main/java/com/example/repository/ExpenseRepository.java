package com.example.repository;

import com.example.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findAllByBranchIdAndCreatedTimeBetweenOrderByCreatedTimeDesc(Integer branchId, LocalDateTime createdTime, LocalDateTime createdTime2);

//    List<Expense> findAllByBranchIdOrOrderByCreatedTimeBranchAsc(Integer branchId, Pageable pageable);

}
