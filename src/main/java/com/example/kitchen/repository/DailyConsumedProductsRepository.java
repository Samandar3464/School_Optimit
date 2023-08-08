package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyConsumedProductsRepository extends JpaRepository<DailyConsumedProducts,Integer> {
}
