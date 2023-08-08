package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.DailyConsumedProducts;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyConsumedProductsResponse {

    private Integer id;

    private String name;

    private boolean active;

    private double quantity;

    private String description;

    private LocalDateTime localDateTime;

    private MeasurementType measurementType;

    private UserResponseDto employee;

    private Branch branch;

    private Warehouse warehouse;

    public static DailyConsumedProductsResponse toEntity(DailyConsumedProducts dailyConsumedProducts){
        return DailyConsumedProductsResponse
                .builder()
                .id(dailyConsumedProducts.getId())
                .name(dailyConsumedProducts.getName())
                .active(dailyConsumedProducts.isActive())
                .quantity(dailyConsumedProducts.getQuantity())
                .description(dailyConsumedProducts.getDescription())
                .localDateTime(dailyConsumedProducts.getLocalDateTime())
                .measurementType(dailyConsumedProducts.getMeasurementType())
                .employee(UserResponseDto.from(dailyConsumedProducts.getEmployee()))
                .branch(dailyConsumedProducts.getBranch())
                .warehouse(dailyConsumedProducts.getWarehouse())
                .build();
    }
}
