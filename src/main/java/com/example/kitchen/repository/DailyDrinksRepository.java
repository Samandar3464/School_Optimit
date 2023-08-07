package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedDrinks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyDrinksRepository extends JpaRepository<DailyConsumedDrinks,Integer> {
    Optional<DailyConsumedDrinks> findByIdAndActiveTrue(Integer id);
    List<DailyConsumedDrinks> findAllByActiveTrue();
    List<DailyConsumedDrinks> findAllByActiveTrueAndBranchId(Integer id);
    List<DailyConsumedDrinks> findAllByActiveTrueAndWarehouseId(Integer id);
}
