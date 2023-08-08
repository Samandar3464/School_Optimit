package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.kitchen.entity.DrinksInWareHouse;
import com.example.kitchen.entity.Warehouse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrinksInWareHouseResponse {

    private Integer id;

    private String name;

    private int count;

    private double totalPrice;

    private int literQuantity;

    private boolean active;

    private Branch branch;

    private Warehouse warehouse;

    public static DrinksInWareHouseResponse toEntity(DrinksInWareHouse drinksInWareHouse) {
        return DrinksInWareHouseResponse
                .builder()
                .id(drinksInWareHouse.getId())
                .active(drinksInWareHouse.isActive())
                .name(drinksInWareHouse.getName())
                .count(drinksInWareHouse.getCount())
                .totalPrice(drinksInWareHouse.getTotalPrice())
                .literQuantity(drinksInWareHouse.getLiterQuantity())
                .branch(drinksInWareHouse.getBranch())
                .warehouse(drinksInWareHouse.getWarehouse())
                .build();
    }
}
