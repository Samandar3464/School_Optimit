package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyProductsRepository extends JpaRepository<DailyConsumedProducts,Integer> {
}
