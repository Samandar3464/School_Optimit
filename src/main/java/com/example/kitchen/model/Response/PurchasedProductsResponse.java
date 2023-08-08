package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.PurchasedProducts;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedProductsResponse {

    private Integer id;

    private String name;

    private double quantity;

    private double unitPrice;

    private double totalPrice;

    private boolean active;

    private String description;

    private LocalDateTime localDateTime;

    private MeasurementType measurementType;

    private UserResponseDto employee;

    private Branch branch;

    private Warehouse warehouse;

    public static PurchasedProductsResponse toResponse(PurchasedProducts purchasedProducts){
        return PurchasedProductsResponse
                .builder()
                .id(purchasedProducts.getId())
                .name(purchasedProducts.getName())
                .quantity(purchasedProducts.getQuantity())
                .unitPrice(purchasedProducts.getUnitPrice())
                .totalPrice(purchasedProducts.getTotalPrice())
                .description(purchasedProducts.getDescription())
                .measurementType(purchasedProducts.getMeasurementType())
                .localDateTime(purchasedProducts.getLocalDateTime())
                .active(purchasedProducts.isActive())
                .employee(UserResponseDto.from(purchasedProducts.getEmployee()))
                .branch(purchasedProducts.getBranch())
                .warehouse(purchasedProducts.getWarehouse())
                .build();
    }
}
