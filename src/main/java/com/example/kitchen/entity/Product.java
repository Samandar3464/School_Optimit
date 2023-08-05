package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double amount;

    private double priceForOne;

    private String description;

    private boolean active;

    private String name;

    private MeasurementType measurementType;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

}
