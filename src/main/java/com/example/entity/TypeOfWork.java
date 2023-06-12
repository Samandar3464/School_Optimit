package com.example.entity;

import com.example.model.request.TypeOfWorkRequest;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TypeOfWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;// asosiy | to'garaklar | vazifa darslar

    private double price;

    public static TypeOfWork toTypeOfWork(TypeOfWorkRequest typeOfWorkRequest){
        return TypeOfWork
                .builder()
                .name(typeOfWorkRequest.getName())
                .price(typeOfWorkRequest.getPrice())
                .build();
    }
}

