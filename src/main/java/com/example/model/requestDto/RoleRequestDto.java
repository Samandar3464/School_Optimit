package com.example.model.requestDto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequestDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private List<Integer> permissionIdList;
}
