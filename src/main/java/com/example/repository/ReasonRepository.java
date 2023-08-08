//package com.example.repository;
//
//import com.example.entity.Reason;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//
//public interface ReasonRepository extends JpaRepository<Reason, Integer> {
//
//    List<Reason> findAllByStudentIdAndActiveTrueOrderByCreateDateAsc(Integer studentId, Sort sort);
//    List<Reason> findAllByBranchIdAndActiveTrueOrderByCreateDateAsc(Integer branchId,Sort sort);
//}
