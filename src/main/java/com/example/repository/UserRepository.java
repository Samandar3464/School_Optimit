package com.example.repository;


import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {


    Optional<User> findByPhoneNumberAndBlockedFalse(String phoneNumber);

    Optional<User> findByPhoneNumberAndVerificationCode(String phoneNumber, Integer verificationCode);

    boolean existsByPhoneNumberAndBlockedFalse(String phoneNumber);

    Optional<User> findByIdAndBlockedFalse(Integer teacherId);
}
