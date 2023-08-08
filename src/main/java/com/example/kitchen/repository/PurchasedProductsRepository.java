package com.example.kitchen.repository;

import com.example.kitchen.entity.PurchasedProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedProductsRepository extends JpaRepository<PurchasedProducts,Integer> {
}
