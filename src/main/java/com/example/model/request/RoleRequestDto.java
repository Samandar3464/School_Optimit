package com.example.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequestDto {

    private Integer id;

    @NotBlank
    private String name;

    private Integer parentId;

    private Integer branchId;

    @NotEmpty
    private List<Integer> permissionIdList;
}
