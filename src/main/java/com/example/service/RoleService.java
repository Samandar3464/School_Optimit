package com.example.service;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.RoleRequestDto;
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
        if (requestDto.getName() == null)
            throw new RecordNotFoundException(Constants.NAME_NOT_FOUND);

        if (roleRepository.findByName(requestDto.getName()).isPresent())
            throw new RecordAlreadyExistException(Constants.ROLE_ALREADY_EXIST);

        Role role = new Role(requestDto.getName(), getPermissionListByIds(requestDto.getPermissionIdList()));
        return new ApiResponse(roleRepository.save(role), true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getRoleByID(Integer id) {
        Role save = roleRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.ROLE_NOT_AVAILABLE));
        return new ApiResponse(save, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(RoleRequestDto requestDto) {
        Role role = roleRepository.findById(requestDto.getId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.ROLE_NOT_AVAILABLE));
        return new ApiResponse(setRole(requestDto, role), true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList() {
        List<Role> all = roleRepository.findAll();
        if (all == null)
            return new ApiResponse(Constants.ROLE_NOT_AVAILABLE, true);

        return new ApiResponse(all, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse remove(Integer id) {
        roleRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.ROLE_NOT_AVAILABLE));
        roleRepository.deleteById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    private Role setRole(RoleRequestDto requestDto, Role role) {
        List<Permission> permissionListByIds = getPermissionListByIds(requestDto.getPermissionIdList());

        if (roleRepository.findByName(requestDto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ROLE_ALREADY_EXIST);
        }
        if (permissionListByIds != null) {
            role.setPermissions(permissionListByIds);
        }
        role.setName(requestDto.getName());

        return roleRepository.save(role);
    }

    private List<Permission> getPermissionListByIds(List<Integer> permissionIdList) {
        List<Permission> permissions = new ArrayList<>();

        if (permissionIdList == null)
            return null;

        for (Integer id : permissionIdList)
            permissions.add(permissionRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException(Constants.PERMISSION_NOT_FOUND)));

        return permissions;
    }
}
