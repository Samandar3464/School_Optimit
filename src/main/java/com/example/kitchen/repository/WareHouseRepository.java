package com.example.kitchen.repository;

import com.example.kitchen.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WareHouseRepository extends JpaRepository<Warehouse,Integer> {
    Optional<Warehouse> findByIdAndActiveTrue(Integer id);

}
