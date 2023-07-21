package com.example.repository;

import com.example.entity.TypeOfWork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeOfWorkRepository extends JpaRepository<TypeOfWork, Integer> {

    List<TypeOfWork> findAllByBranchIdAndActiveTrue(Integer branchId);

}
