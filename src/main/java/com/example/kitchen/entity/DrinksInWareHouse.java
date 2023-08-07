package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.kitchen.model.request.DrinkRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DrinksInWareHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int count;

    private int literQuantity;

    private boolean active;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

    public static DrinksInWareHouse toEntity(DrinkRequest drinkRequest) {
        return DrinksInWareHouse
                .builder()
                .count(drinkRequest.getCount())
                .literQuantity(drinkRequest.getLiterQuantity())
                .active(true)
                .name(drinkRequest.getName())
                .build();
    }
}
