package com.example.controller;

import com.example.entity.Permission;
import com.example.model.common.ApiResponse;
import com.example.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permission/")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("addPermission")
    public ApiResponse addPermission(@RequestBody @Valid Permission permission) {
        return permissionService.create(permission);
    }

    @PutMapping("updatePermission")
    public ApiResponse updatePermission(@RequestBody @Valid Permission permission) {
        return permissionService.update(permission);
    }

    @DeleteMapping("deletePermission/{id}")
    public ApiResponse deletePermission(@PathVariable Integer id) {
        return permissionService.delete(id);
    }

    @GetMapping("getPermissionList")
    public ApiResponse getList() {
        return permissionService.getList();
    }

    @GetMapping("getById/{id}")
    public ApiResponse getList(@PathVariable Integer id) {
        return permissionService.getById(id);
    }
}
