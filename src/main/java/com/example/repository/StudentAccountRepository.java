package com.example.repository;

import com.example.entity.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAccountRepository extends JpaRepository<StudentAccount , Integer> {


    Optional<StudentAccount> findByStudentIdAndActiveTrue(Integer integer);
    Optional<StudentAccount> findByStudentId(Integer integer);

    List<StudentAccount> findAllByBranchIdAndActiveTrueOrderByCreatedDateAsc(Integer integer);
}
