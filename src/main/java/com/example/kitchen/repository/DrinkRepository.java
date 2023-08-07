package com.example.kitchen.repository;

import com.example.kitchen.entity.DrinksInWareHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrinkRepository extends JpaRepository<DrinksInWareHouse,Integer> {
//    Optional<DrinksInWareHouse> findByNameAndActiveTrueAndLiterQuantityAndWarehouseId(String name, int literQuantity, Integer warehouse_id);
}
