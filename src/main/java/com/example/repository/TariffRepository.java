package com.example.repository;

import com.example.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff,Integer> {
    List<Tariff> findAllByActiveTrueAndDeleteFalseOrderByPrice();
    Optional<Tariff> findByIdAndDeleteFalse(Integer id);
    List<Tariff> findAllByDeleteFalseOrderByPrice();
}
