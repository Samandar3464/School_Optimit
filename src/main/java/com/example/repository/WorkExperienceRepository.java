package com.example.repository;

import com.example.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience,Integer> {
//    Optional<WorkExperience> findByPlaceOfWorkAndPositionAndEmployeeId(String placeOfWork, String position, Integer employeeId);
    Optional<WorkExperience> findByPlaceOfWorkAndPositionAndEmployeeIdAndStartDateAfterAndEndDateBefore(String placeOfWork, String position, Integer employeeId, LocalDate startDate, LocalDate endDate);
    Optional<WorkExperience> findByPosition(String position);

    List<WorkExperience> findAllByEmployeeId(Integer employeeId);

}
