package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.requestDto.RoleRequestDto;
import com.example.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ROLE_ACCESS')")
    public ApiResponse save(@RequestBody RoleRequestDto requestDto) {
        return roleService.save(requestDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ACCESS')")
    public ApiResponse update(@RequestBody RoleRequestDto requestDto) {
        return roleService.update(requestDto);
    }

    @PutMapping("/getRoleByID/{id}")
    @PreAuthorize("hasAuthority('ROLE_ACCESS')")
    public ApiResponse getRoleByID(@PathVariable Integer id) {
        return roleService.getRoleByID(id);
    }

    @PutMapping("/getList")
    @PreAuthorize("hasAuthority('ROLE_ACCESS')")
    public ApiResponse getList() {
        return roleService.getList();
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasAuthority('ROLE_ACCESS')")
    public ApiResponse remove(@PathVariable Integer id) {
        return roleService.remove(id);
    }
}
