package com.example.repository;

import com.example.entity.Family;
import com.example.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findAllByStudentClassIdAndBranchIdAndActiveTrue(Integer studentClassId, Integer branchId, Sort sort);

    Page<Student> findAllByBranchIdAndActiveTrue(Pageable pageable, Integer id);

    List<Student> findAllByBranchIdAndActiveFalseOrderByAddedTimeAsc(Integer branchId,Sort pageable);
    List<Student> findAllByBranchIdAndActiveTrue(Integer branchId,Pageable pageable);


    List<Student> findByFamiliesIn(Collection<List<Family>> families, Sort sort);

    Optional<Student> findByIdAndActiveTrue(Integer id);
    Optional<Student> findByAccountNumberAndActiveTrue(String accountNumber);

    Optional<Student> findByPhoneNumberAndPassword(String username, String password);
}
