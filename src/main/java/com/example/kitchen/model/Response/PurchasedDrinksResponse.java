package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.kitchen.entity.PurchasedDrinks;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedDrinksResponse {

    private Integer id;

    private String name;

    private int count;

    private double unitPrice;

    private double totalPrice;

    private int literQuantity;

    private String description;

    private boolean active;

    private LocalDateTime localDateTime;

    private UserResponseDto employee;

    private Branch branch;

    private Warehouse warehouse;

    public static PurchasedDrinksResponse toResponse(PurchasedDrinks purchasedDrinks) {
        return PurchasedDrinksResponse
                .builder()
                .id(purchasedDrinks.getId())
                .count(purchasedDrinks.getCount())
                .unitPrice(purchasedDrinks.getUnitPrice())
                .totalPrice(purchasedDrinks.getTotalPrice())
                .literQuantity(purchasedDrinks.getLiterQuantity())
                .description(purchasedDrinks.getDescription())
                .active(purchasedDrinks.isActive())
                .name(purchasedDrinks.getName())
                .localDateTime(purchasedDrinks.getLocalDateTime())
                .employee(UserResponseDto.from(purchasedDrinks.getEmployee()))
                .branch(purchasedDrinks.getBranch())
                .warehouse(purchasedDrinks.getWarehouse())
                .build();
    }
}
