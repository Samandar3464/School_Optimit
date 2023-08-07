package com.example.kitchen.repository;

import com.example.kitchen.entity.ProductsInWareHouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductsInWareHouse,Integer> {
}
