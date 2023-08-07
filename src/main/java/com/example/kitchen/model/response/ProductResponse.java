package com.example.kitchen.model.response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Integer id;

    private double quantity;

    private double unitPrice;

    private double totalPrice;

    private String description;

    private boolean active;

    private String name;

    private String localDateTime;

    private MeasurementType measurementType;

    private Branch branch;

    private Warehouse warehouse;
}
