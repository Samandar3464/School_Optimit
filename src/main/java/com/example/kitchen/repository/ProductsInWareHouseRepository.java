package com.example.kitchen.repository;

import com.example.enums.MeasurementType;
import com.example.kitchen.entity.ProductsInWareHouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductsInWareHouseRepository extends JpaRepository<ProductsInWareHouse, Integer> {
    Optional<ProductsInWareHouse> findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(String name, MeasurementType measurementType,Integer branchId,Integer wareHouseId);
}
