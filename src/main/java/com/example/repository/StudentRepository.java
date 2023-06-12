package com.example.repository;

import com.example.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByStudentClassIdAndBranchIdAndActiveTrue(Integer studentClass_id, Integer branch_id);

    Page<Student> findAllByBranchIdAndActiveTrue(Pageable pageable, Integer id);

    List<Student> findAllByBranchIdAndActiveFalseOrderByAddedTimeAsc(Integer branch_id);
}
