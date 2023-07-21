package com.example.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TypeOfWorkRequest {

    private Integer id;

    private String name;// asosiy | to'garaklar | vazifa darslar

    private double priceForPerHour;

    private Integer branchId;
}
