package com.example.repository;

import com.example.entity.Family;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FamilyRepository extends JpaRepository<Family,Integer> {
    Page<Family> findAllByBranchIdAndActiveTrue(int branch_id,Pageable pageable);

    boolean existsByPhoneNumber(@NotBlank @Size(min = 9, max = 9) String phoneNumber);

}
