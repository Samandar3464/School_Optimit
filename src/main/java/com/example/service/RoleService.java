package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.RoleRequestDto;
import com.example.model.response.RoleResponseList;
import com.example.repository.BranchRepository;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class RoleService implements BaseService<RoleRequestDto, Integer> {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(RoleRequestDto requestDto) {
        if (roleRepository.findByNameAndActiveTrue(requestDto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ROLE_ALREADY_EXIST);
        }
        Role role = from(requestDto);
        return new ApiResponse(roleRepository.save(role), true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(roleRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new RecordNotFoundException(ROLE_NOT_FOUND)), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(RoleRequestDto dto) {
        if (roleRepository.findByNameAndActiveTrue(dto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.ROLE_ALREADY_EXIST);
        }
        Role role = roleRepository.findByIdAndActiveTrue(dto.getId()).orElseThrow(() -> new RecordNotFoundException(ROLE_NOT_FOUND));
        Role parentRole = roleRepository.findById(dto.getParentId()).orElseThrow(() -> new RecordNotFoundException(PARENT_ROLE_NOT_FOUND));
        List<Permission> permissionList = permissionRepository.findAllById(dto.getPermissionIdList());
        role.setName(dto.getName());
        role.setParentRole(parentRole);
        role.setPermissions(permissionList);
        return new ApiResponse(roleRepository.save(role), true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.ROLE_NOT_AVAILABLE));
        role.setActive(false);
        roleRepository.save(role);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList(int size, int page,int branchId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findAllByBranchIdAndActiveTrue(branchId,pageable);
        return new ApiResponse(new RoleResponseList(roles.getContent(), roles.getTotalElements(), roles.getTotalPages(), roles.getNumberOfElements()), true);
    }

    private Role from(RoleRequestDto requestDto) {
        Role parentRole = roleRepository.findById(requestDto.getParentId()).orElseThrow(() -> new RecordNotFoundException(PARENT_ROLE_NOT_FOUND));
        Branch branch = branchRepository.findById(requestDto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        return Role
                .builder()
                .name(requestDto.getName())
                .parentRole(parentRole)
                .permissions(permissionRepository.findAllById(requestDto.getPermissionIdList()))
                .branch(branch)
                .active(true)
                .build();
    }
}
