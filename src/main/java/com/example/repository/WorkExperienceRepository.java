package com.example.repository;

import com.example.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience,Integer> {
    Optional<WorkExperience> findByPlaceOfWorkAndPositionAndEmployeeIdAndStartDateAndEndDate(String placeOfWork, String position, Integer employee_id, LocalDate startDate, LocalDate endDate);
    Optional<WorkExperience> findByPosition(String position);

    List<WorkExperience> findAllByEmployee(int employee);

}
