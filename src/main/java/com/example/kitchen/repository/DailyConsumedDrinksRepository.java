package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedDrinks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyConsumedDrinksRepository extends JpaRepository<DailyConsumedDrinks,Integer> {
}
