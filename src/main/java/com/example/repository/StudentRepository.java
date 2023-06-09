package com.example.repository;

import com.example.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByStudentClassIdAndActiveTrue(Integer studentClass_id);

    Page<Student> findAllByActiveTrue(Pageable pageable);

    List<Student> findAllByActiveFalseOrderByAddedTimeAsc();
}
