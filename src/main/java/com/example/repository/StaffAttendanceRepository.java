package com.example.repository;

import com.example.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance,Integer> {

   List<StaffAttendance> findAllByUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(Integer userId, LocalDateTime startTime, LocalDateTime endTime);
   List<StaffAttendance> findAllByBranchIdAndCreatedDateBetweenOrderByCreatedDateDesc(Integer branchId, LocalDateTime createdDate, LocalDateTime createdDate2);
}
