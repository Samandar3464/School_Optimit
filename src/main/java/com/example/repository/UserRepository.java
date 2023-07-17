package com.example.repository;


import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {


    Optional<User> findByPhoneNumberAndDeletedFalse(String phoneNumber);

    Optional<User> findByPhoneNumberAndVerificationCodeAndDeletedFalse(String phoneNumber, Integer verificationCode);

    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);

    Page<User> findAllByBranchIdAndDeletedFalse(Integer branchId, Pageable pageable);

    Optional<User> findByIdAndDeletedFalse(Integer id);

}
