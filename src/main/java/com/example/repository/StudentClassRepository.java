package com.example.repository;

import com.example.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
    List<StudentClass> findAllByActiveTrue();
    List<StudentClass> findAllByStartDateAfterAndEndDateBeforeAndActiveFalse(LocalDate startDate, LocalDate endDate);
}
