package com.example.kitchen.model.request;

import com.example.enums.MeasurementType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private Integer id;

    private double quantity;

    private double unitPrice;

    private double totalPrice;

    private String description;

    private String name;

    @Enumerated(EnumType.STRING)
    private MeasurementType measurementType;

    private Integer branchId;

    private Integer warehouseId;
}
