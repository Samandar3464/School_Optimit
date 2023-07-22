package com.example.repository;

import com.example.entity.SubjectLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel,Integer> {
    List<SubjectLevel> findAllByBranchId(Integer branchId);
}
