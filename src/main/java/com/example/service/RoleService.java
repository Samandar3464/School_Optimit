package com.example.service;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.enums.Constants;
import com.example.exception.RoleNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.requestDto.RoleRequestDto;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;


    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse save(RoleRequestDto requestDto) {
        if (requestDto.getName() == null) {
            throw new RoleNotFoundException(Constants.NAME_NOT_FOUND);
        }
        return new ApiResponse(getSave(requestDto), true);
    }

    private Role getSave(RoleRequestDto requestDto) {
        List<Permission> permissions = new ArrayList<>();
        for (Integer id : requestDto.getPermissionIdList()) {
            permissions.add(permissionRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(Constants.PERMISSION_NOT_FOUND)));
        }
        Role role = new Role(requestDto.getName(), permissions);
        return roleRepository.save(role);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getRoleByID(Integer id) {
        Role save = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(Constants.ID_NOT_FOUND));
        return new ApiResponse(save, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(RoleRequestDto requestDto) {
        Role role = roleRepository.findById(requestDto.getId())
                .orElseThrow(() -> new RoleNotFoundException(Constants.ID_NOT_FOUND));
        role.setName(requestDto.getName());
        return new ApiResponse(roleRepository.save(role), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList() {
        List<Role> all = roleRepository.findAll();
        return new ApiResponse(all, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse remove(Integer id) {
        roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(Constants.ID_NOT_FOUND));
        roleRepository.deleteById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }
}
