package com.example.repository;

import com.example.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface JournalRepository extends JpaRepository<Journal, Integer> {

    List<Journal> findAllByBranchIdAndActiveTrue(Integer branchId);
    boolean existsByStudentClassIdAndActiveTrue(Integer studentClassId);
}
