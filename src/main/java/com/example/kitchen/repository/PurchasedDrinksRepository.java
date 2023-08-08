package com.example.kitchen.repository;

import com.example.kitchen.entity.PurchasedDrinks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedDrinksRepository extends JpaRepository<PurchasedDrinks,Integer> {
}
