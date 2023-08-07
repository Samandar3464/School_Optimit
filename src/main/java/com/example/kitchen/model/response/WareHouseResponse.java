package com.example.kitchen.model.response;

import com.example.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WareHouseResponse {

    private Integer id;

    private String name;

    private Branch branch;

    private boolean active;
}
