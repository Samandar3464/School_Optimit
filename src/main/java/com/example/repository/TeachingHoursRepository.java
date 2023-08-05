package com.example.repository;

import com.example.entity.TeachingHours;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TeachingHoursRepository extends JpaRepository<TeachingHours,Integer> {
    Optional<TeachingHours> findByTeacherIdAndDateAndLessonHoursAndActiveTrue(Integer teacher_id, LocalDate date, int lessonHours);

    List<TeachingHours> findAllByTeacherIdAndActiveTrue(Integer teacherId, Sort sort);
    List<TeachingHours> findAllByTeacherIdAndActiveTrueAndDateBetween(Integer teacher_id, LocalDate date, LocalDate date2,Sort sort);

    Optional<TeachingHours> findByIdAndActiveTrue(Integer integer);

}
