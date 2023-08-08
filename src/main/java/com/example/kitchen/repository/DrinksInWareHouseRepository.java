package com.example.kitchen.repository;

import com.example.kitchen.entity.DrinksInWareHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrinksInWareHouseRepository extends JpaRepository<DrinksInWareHouse, Integer> {
    Optional<DrinksInWareHouse> findByNameAndLiterQuantityAndBranchIdAndWarehouseIdAndActiveTrue(String name, int literQuantity, Integer branchId, Integer warehouseId);

    Optional<DrinksInWareHouse> findByIdAndActiveTrue(Integer integer);
}
