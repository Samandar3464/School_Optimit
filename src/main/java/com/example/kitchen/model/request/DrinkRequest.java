package com.example.kitchen.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrinkRequest {

    private Integer id;

    private int count;

    private double unitPrice;

    private double totalPrice;

    private int literQuantity;

    private String description;

    private String name;

    private Integer branchId;

    private Integer warehouseId;
}
