package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.example.kitchen.model.request.ProductsInWareHouseRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ProductsInWareHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private double quantity;

    private double totalPrice;

    private MeasurementType measurementType;

    private boolean active;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

    public static ProductsInWareHouse toEntity(ProductsInWareHouseRequest request){
        return ProductsInWareHouse
                .builder()
                .name(request.getName())
                .active(true)
                .quantity(request.getQuantity())
                .measurementType(request.getMeasurementType())
                .totalPrice(request.getTotalPrice())
                .build();
    }
}