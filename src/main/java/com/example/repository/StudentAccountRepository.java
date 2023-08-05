package com.example.repository;

import com.example.entity.StudentAccount;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAccountRepository extends JpaRepository<StudentAccount,Integer> {
    Optional<StudentAccount> findByAccountNumberAndActiveTrue(String accountNumber);
    Optional<StudentAccount> findByIdAndActiveTrue(Integer id);
    List<StudentAccount> findAllByBranch_IdAndActiveTrue(Integer id, Sort sort);

    List<StudentAccount> findAllByActiveTrueAndAmountOfDebitIsNotNull(Sort sort);
    List<StudentAccount> findAllByActiveTrue(Sort sort);
}
