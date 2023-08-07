package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.entity.User;
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
public class PurchasedDrinks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int count;

    private double unitPrice;

    private double totalPrice;

    private int literQuantity;

    private String description;

    private boolean active;

    private LocalDateTime localDateTime;

    @ManyToOne
    private User employee;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

    public static PurchasedDrinks toEntity(DrinkRequest drinkRequest) {
        return PurchasedDrinks
                .builder()
                .count(drinkRequest.getCount())
                .unitPrice(drinkRequest.getUnitPrice())
                .totalPrice(drinkRequest.getTotalPrice())
                .literQuantity(drinkRequest.getLiterQuantity())
                .description(drinkRequest.getDescription())
                .active(true)
                .name(drinkRequest.getName())
                .localDateTime(LocalDateTime.now())
                .build();
    }
}
