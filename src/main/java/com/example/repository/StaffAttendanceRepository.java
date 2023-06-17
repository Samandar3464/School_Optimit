package com.example.repository;

import com.example.entity.StaffAttendance;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance,Integer> {

    List<StaffAttendance> findAllByUserAndDateBetweenAndCameToWorkTrue(User user, LocalDate date, LocalDate date2);
}
