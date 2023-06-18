package com.example.kitchen.repository;

import com.example.kitchen.entity.ProductAndQuantity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAndQuantityRepository extends JpaRepository<ProductAndQuantity, Integer> {
    List<ProductAndQuantity> findAllByWarehouseId(Integer warehouse_id);

    Optional<ProductAndQuantity> findByWarehouseIdAndProductId(Integer warehouse_id, Integer product_id);
}
