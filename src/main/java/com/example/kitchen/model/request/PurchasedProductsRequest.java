package com.example.kitchen.model.request;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.enums.MeasurementType;
import com.example.kitchen.entity.Warehouse;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class PurchasedProductsRequest {
    private Integer id;

    private String name;

    private double quantity;

    private double unitPrice;

    private double totalPrice;

    private String description;

    private LocalDateTime localDateTime;

    private MeasurementType measurementType;

    private User employee;

    private Branch branch;

    private Warehouse warehouse;
}
