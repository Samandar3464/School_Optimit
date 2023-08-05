package com.example.repository;

import com.example.entity.StaffAttendance;
import com.example.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance,Integer> {

    List<StaffAttendance> findAllByUserId(Integer id, Sort sort);

    Optional<StaffAttendance> findByUserIdAndDate(Integer user_id, LocalDate date);

    List<StaffAttendance> findAllByBranchId(Integer id,Sort sort);
}
