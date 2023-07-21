package com.example.repository;

import com.example.entity.AdditionalExpense;
import com.example.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AdditionalExpenseRepository extends JpaRepository<AdditionalExpense, Integer> {

    List<AdditionalExpense> findAllByBranchIdAndExpenseTypeAndCreatedTimeBetweenOrderByCreatedTimeDesc(Integer branchId, ExpenseType expenseType, LocalDateTime createdTime, LocalDateTime createdTime2);


}
