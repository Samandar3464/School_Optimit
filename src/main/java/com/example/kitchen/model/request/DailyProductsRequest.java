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
public class DailyProductsRequest {

    private Integer id;

    private String name;

    private double quantity;

    private String description;

    @Enumerated(EnumType.STRING)
    private MeasurementType measurementType;

    private Integer branchId;

    private Integer warehouseId;
}
