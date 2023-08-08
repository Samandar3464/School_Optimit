package com.example.kitchen.repository;

import com.example.kitchen.entity.PurchasedProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchasedProductsRepository extends JpaRepository<PurchasedProducts,Integer> {
    Optional<PurchasedProducts> findByIdAndActiveTrue(Integer integer);
}
