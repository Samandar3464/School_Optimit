package com.example.config;

import com.example.entity.Permission;
import com.example.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PermissionService permissionService;

    @Override
    public void run(String... args) throws Exception {

        permissionService.save(new Permission("ROLE_ACCESS"));

    }
}
