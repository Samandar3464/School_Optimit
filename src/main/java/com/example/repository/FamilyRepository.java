package com.example.repository;

import com.example.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyRepository extends JpaRepository<Family,Integer> {
    List<Family> findAllByActiveTrue();

}
