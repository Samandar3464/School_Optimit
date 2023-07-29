package com.example.repository;

import com.example.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,Integer> {
    Optional<TransactionHistory> findByIdAndActiveTrue(Integer integer);
    List<TransactionHistory> findAllByBranch_IdAndActiveTrue(Integer branchId);

    List<TransactionHistory> findAllByActiveTrue();
}
