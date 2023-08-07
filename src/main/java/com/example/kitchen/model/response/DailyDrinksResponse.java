package com.example.kitchen.model.response;

import com.example.entity.Branch;
import com.example.kitchen.entity.DailyConsumedDrinks;
import com.example.kitchen.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyDrinksResponse {

    private Integer id;

    private String name;

    private String description;

    private int literQuantity;

    private int count;

    private String localDateTime;

    private Branch branch;

    private Warehouse warehouse;

    public static DailyDrinksResponse toResponse(DailyConsumedDrinks dailyConsumedDrinks) {
        return DailyDrinksResponse
                .builder()
                .id(dailyConsumedDrinks.getId())
                .name(dailyConsumedDrinks.getName())
                .description(dailyConsumedDrinks.getDescription())
                .literQuantity(dailyConsumedDrinks.getLiterQuantity())
                .count(dailyConsumedDrinks.getCount())
                .localDateTime(dailyConsumedDrinks.getLocalDateTime().toString())
                .branch(dailyConsumedDrinks.getBranch())
                .warehouse(dailyConsumedDrinks.getWarehouse())
                .build();
    }

    public static List<DailyDrinksResponse> toAllResponse(List<DailyConsumedDrinks> dailyConsumedDrinksList) {
        List<DailyDrinksResponse> dailyDrinksResponses = new ArrayList<>();
        dailyConsumedDrinksList.forEach(dailyConsumedDrinks -> {
            dailyDrinksResponses.add(toResponse(dailyConsumedDrinks));
        });
        return dailyDrinksResponses;
    }
}
