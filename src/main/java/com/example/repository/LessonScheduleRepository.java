package com.example.repository;

import com.example.entity.LessonSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LessonScheduleRepository  extends JpaRepository<LessonSchedule ,Integer> {

    Optional<LessonSchedule> findByBranchIdAndTeacherIdAndStartTime(Integer branchId, Integer teacherId, LocalDateTime startTime);
    Optional<LessonSchedule> findByBranchIdAndStudentClassIdAndStartTime(Integer branchId, Integer studentClassId, LocalDateTime startTime);
    Optional<LessonSchedule> findByBranchIdAndRoomIdAndStartTime(Integer branchId, Integer roomId, LocalDateTime startTime);

    List<LessonSchedule> findAllByBranchIdAndActiveTrue(Integer branch_id);
    List<LessonSchedule> findAllByBranchIdAndStartTimeBetweenAndActiveTrue(Integer branch_id, LocalDateTime startTime, LocalDateTime startTime2);
}
