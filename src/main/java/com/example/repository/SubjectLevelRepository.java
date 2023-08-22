package com.example.repository;

import com.example.entity.SubjectLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel,Integer> {
    Optional<SubjectLevel> findByIdAndActiveTrue(Integer subjectId);

    Optional<SubjectLevel> findByBranchIdAndSubjectIdAndLevelIdAndActiveTrue(Integer branch_id, Integer subject_id, Integer level_id);

    List<SubjectLevel> findAllByBranch_IdAndActiveTrue(Integer id);
}
