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
public class DailyProductsResponse {

    private Integer id;

    private String name;

    private double quantity;

    private String description;

    private String localDateTime;

    private MeasurementType measurementType;

    private Branch branch;

    private Warehouse warehouse;
}
