package com.example.repository;

import com.example.entity.TeachingHours;
import com.example.entity.TypeOfWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeachingHoursRepository extends JpaRepository<TeachingHours, UUID> {
    List<TeachingHours> findAllByTeacherIdAndActiveTrueAndPassedDateBetween(Integer teacherId, LocalDate startDate, LocalDate endDate);
    List<TeachingHours> findAllByTeacherIdAndActiveTrue(Integer teacherId);
    List<TeachingHours> findAllByTypeOfWorkIdAndActiveTrueAndPassedDateBetween(Integer typeOfWorkId, LocalDate startDate, LocalDate endDate);
}
