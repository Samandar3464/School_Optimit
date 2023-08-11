package com.example.kitchen.repository;

import com.example.kitchen.entity.DailyConsumedProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DailyConsumedProductsRepository extends JpaRepository<DailyConsumedProducts,Integer> {
    Optional<DailyConsumedProducts> findByIdAndActiveTrue(Integer integer);

    Page<DailyConsumedProducts> findAllByWarehouseIdAndActiveTrue(Integer warehouseId, Pageable pageable);

    Page<DailyConsumedProducts> findAllByBranchIdAndActiveTrue(Integer branchId, Pageable pageable);
}
