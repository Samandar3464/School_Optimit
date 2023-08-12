package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Data
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
}
