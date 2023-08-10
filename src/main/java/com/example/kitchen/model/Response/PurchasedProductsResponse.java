package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Data
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

}
