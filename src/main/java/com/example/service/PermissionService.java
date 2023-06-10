package com.example.service;

import com.example.entity.Permission;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public void save(Permission permission) {
        if (permission == null) {
            throw new RecordNotFoundException(Constants.PERMISSION_NOT_FOUND);
        }
        permissionRepository.save(permission);
    }

    public ApiResponse getByID(Integer id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.PERMISSION_NOT_FOUND));
        return new ApiResponse(permission, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(Integer id,String name) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.PERMISSION_NOT_FOUND));
        permission.setName(name);
        return new ApiResponse(permissionRepository.save(permission), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList() {
        List<Permission> all = permissionRepository.findAll();
        return new ApiResponse(all, true);
    }

    public boolean getList1() {
        boolean flag = true;
        List<Permission> all = permissionRepository.findAll();
        for (Permission a:all) {
            if (a!=null){
                flag=false;
            }
        }
        return flag;
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse remove(Integer id) {
        permissionRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.PERMISSION_NOT_FOUND));
        permissionRepository.deleteById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }
}
