package com.example.repository;

import com.example.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience,Integer> {
    Optional<WorkExperience> findByPlaceOfWork(String placeOfWork);
    Optional<WorkExperience> findByPosition(String position);

}
