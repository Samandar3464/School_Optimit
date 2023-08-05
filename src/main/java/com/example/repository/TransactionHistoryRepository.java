package com.example.repository;

import com.example.entity.TransactionHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Integer> {
    Optional<TransactionHistory> findByIdAndActiveTrue(Integer integer);
    Optional<TransactionHistory> findByStudentIdAndActiveTrue(Integer integer);
    List<TransactionHistory> findAllByBranch_IdAndActiveTrue(Integer branchId, Sort sort);
    List<TransactionHistory> findAllByActiveTrue(Sort sort);
}
