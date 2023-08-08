package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.ProductsInWareHouse;
import com.example.kitchen.entity.Warehouse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductsInWareHouseResponse {

    private Integer id;

    private String name;

    private double quantity;

    private double totalPrice;

    private MeasurementType measurementType;

    private boolean active;

    private Branch branch;

    private Warehouse warehouse;

    public static ProductsInWareHouseResponse toEntity(ProductsInWareHouse productsInWareHouse){
        return ProductsInWareHouseResponse
                .builder()
                .id(productsInWareHouse.getId())
                .name(productsInWareHouse.getName())
                .active(productsInWareHouse.isActive())
                .quantity(productsInWareHouse.getQuantity())
                .measurementType(productsInWareHouse.getMeasurementType())
                .totalPrice(productsInWareHouse.getTotalPrice())
                .branch(productsInWareHouse.getBranch())
                .warehouse(productsInWareHouse.getWarehouse())
                .build();
    }
}