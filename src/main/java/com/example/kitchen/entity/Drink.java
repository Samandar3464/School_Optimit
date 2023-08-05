package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.enums.MeasurementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double unitPrice;

    private int count;

    private int literQuantity;

    private String description;

    private boolean active;

    private String name;

    private MeasurementType measurementType;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;
}
