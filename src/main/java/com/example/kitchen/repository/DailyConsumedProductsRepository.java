package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyConsumedProductsRepository extends JpaRepository<DailyConsumedProducts,Integer> {
    Optional<DailyConsumedProducts> findByIdAndActiveTrue(Integer integer);
}
