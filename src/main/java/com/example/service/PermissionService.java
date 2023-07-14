package com.example.service;

import com.example.entity.Permission;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class PermissionService implements BaseService<Permission, Integer> {

    private final PermissionRepository permissionRepository;

    @Override
    public ApiResponse create(Permission permission) {
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new RecordAlreadyExistException(PERMISSION_ALREADY_EXIST);
        }
        return new ApiResponse(permissionRepository.save(permission), true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(permissionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(PERMISSION_NOT_FOUND)), true);
    }

    @Override
    public ApiResponse update(Permission newPermission) {
        if (permissionRepository.findByName(newPermission.getName()).isPresent()) {
            throw new RecordAlreadyExistException(PERMISSION_ALREADY_EXIST);
        }
        Permission permission = permissionRepository.findById(newPermission.getId()).orElseThrow(() -> new RecordNotFoundException(PERMISSION_NOT_FOUND));
        permission.setName(newPermission.getName());
        permissionRepository.save(newPermission);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        permissionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(PERMISSION_NOT_FOUND));
        permissionRepository.deleteById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList() {
        return new ApiResponse(permissionRepository.findAll(), true);
    }
}
