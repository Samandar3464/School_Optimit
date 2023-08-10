package com.example.kitchen.repository;

import com.example.kitchen.entity.PurchasedDrinks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchasedDrinksRepository extends JpaRepository<PurchasedDrinks,Integer> {
    Optional<PurchasedDrinks> findByIdAndActiveTrue(Integer integer);
    Page<PurchasedDrinks> findAllByWarehouseIdAndActiveTrue(Integer integer, Pageable pageable);
    Page<PurchasedDrinks> findAllByBranch_IdAndActiveTrue(Integer integer,Pageable pageable);
}
