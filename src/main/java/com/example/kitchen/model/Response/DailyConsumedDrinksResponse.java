package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.kitchen.entity.DailyConsumedDrinks;
import com.example.kitchen.entity.Warehouse;
import com.example.model.response.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyConsumedDrinksResponse {

    private Integer id;

    private String name;

    private int literQuantity;

    private int count;

    private String description;

    private boolean active;

    private LocalDateTime localDateTime;

    private UserResponseDto employee;

    private Branch branch;

    private Warehouse warehouse;

    public static DailyConsumedDrinksResponse toResponse(DailyConsumedDrinks dailyConsumedDrinks) {
        return DailyConsumedDrinksResponse
                .builder()
                .id(dailyConsumedDrinks.getId())
                .name(dailyConsumedDrinks.getName())
                .description(dailyConsumedDrinks.getDescription())
                .literQuantity(dailyConsumedDrinks.getLiterQuantity())
                .count(dailyConsumedDrinks.getCount())
                .localDateTime(dailyConsumedDrinks.getLocalDateTime())
                .active(dailyConsumedDrinks.isActive())
                .employee(UserResponseDto.from(dailyConsumedDrinks.getEmployee()))
                .branch(dailyConsumedDrinks.getBranch())
                .warehouse(dailyConsumedDrinks.getWarehouse())
                .build();
    }
}
