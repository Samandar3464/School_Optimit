package com.example.repository;

import com.example.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission,Integer> {
    Optional<Permission> findByName(String name);
}
