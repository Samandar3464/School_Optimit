package com.example.repository;

import com.example.entity.TeachingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TeachingHoursRepository extends JpaRepository<TeachingHours,Integer> {
    Optional<TeachingHours> findByTeacherIdAndDateAndLessonHours(Integer teacher_id, LocalDate date, int lessonHours);

    List<TeachingHours> findAllByTeacherIdAndActiveTrue(Integer teacherId);
    List<TeachingHours> findAllByTeacherIdAndDateBetween(Integer teacher_id, LocalDate date, LocalDate date2);
}
