package com.example.entity;

import com.example.model.request.TypeOfWorkRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


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

    private double priceForPerHour;

    private boolean active;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Branch branch;
    public static TypeOfWork toTypeOfWork(TypeOfWorkRequest typeOfWorkRequest,Branch branch) {
        return TypeOfWork
                .builder()
                .branch(branch)
                .name(typeOfWorkRequest.getName())
                .priceForPerHour(typeOfWorkRequest.getPriceForPerHour())
                .active(true)
                .build();
    }
}

