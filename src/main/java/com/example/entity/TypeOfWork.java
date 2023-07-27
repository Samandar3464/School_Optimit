package com.example.entity;

import com.example.enums.SalaryType;
import com.example.model.request.TypeOfWorkRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;


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

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;


    public static TypeOfWork toTypeOfWork(TypeOfWorkRequest typeOfWorkRequest) {
        return TypeOfWork
                .builder()
                .name(typeOfWorkRequest.getName())
                .price(typeOfWorkRequest.getPrice())
                .build();
    }
}

