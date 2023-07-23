package com.example.repository;

import com.example.entity.SubjectLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel,Integer> {
    List<SubjectLevel> findAllByBranchId(Integer branchId);
    Optional<SubjectLevel> findBySubjectIdAndLevelId(Integer subjectI, Integer levelId);
}
