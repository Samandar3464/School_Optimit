package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.kitchen.model.request.DailyConsumedDrinksRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DailyConsumedDrinks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int literQuantity;

    private int count;

    private String description;

    private boolean active;

    private LocalDateTime localDateTime;

    @ManyToOne
    private User employee;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

    public static DailyConsumedDrinks toEntity(DailyConsumedDrinksRequest request) {
        return DailyConsumedDrinks
                .builder()
                .name(request.getName())
                .description(request.getDescription())
                .literQuantity(request.getLiterQuantity())
                .count(request.getCount())
                .localDateTime(LocalDateTime.now())
                .active(true)
                .build();
    }
}
