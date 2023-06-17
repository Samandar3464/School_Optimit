package com.example.repository;

import com.example.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience,Integer> {
    Optional<WorkExperience> findByPlaceOfWorkAndPosition(String placeOfWork, String position);
    Optional<WorkExperience> findByPosition(String position);

    List<WorkExperience> findAllByEmployee(int employee);

}
