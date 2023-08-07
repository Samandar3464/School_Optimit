package com.example.kitchen.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyDrinksRequest {

    private Integer id;

    private String name;

    private String description;

    private int literQuantity;

    private int count;

    private Integer branchId;

    private Integer warehouseId;
}
