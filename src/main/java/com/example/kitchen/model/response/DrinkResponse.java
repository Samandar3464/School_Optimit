//package com.example.kitchen.model.response;
//
//import com.example.entity.Branch;
//import com.example.kitchen.entity.DrinksInWareHouse;
//import com.example.kitchen.entity.Warehouse;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class DrinkResponse {
//
//    private Integer id;
//
//    private int count;
//
//    private double unitPrice;
//
//    private double totalPrice;
//
//    private int literQuantity;
//
//    private String description;
//
//    private boolean active;
//
//    private String name;
//
//    private String localDateTime;
//
//    private Branch branch;
//
//    private Warehouse warehouse;
//
//    public static DrinkResponse toResponse(DrinksInWareHouse drinksInWareHouse) {
//        return DrinkResponse
//                .builder()
//                .id(drinksInWareHouse.getId())
//                .count(drinksInWareHouse.getCount())
//                .literQuantity(drinksInWareHouse.getLiterQuantity())
//                .active(drinksInWareHouse.isActive())
//                .unitPrice(drinksInWareHouse.getUnitPrice())
//                .totalPrice(drinksInWareHouse.getTotalPrice())
//                .description(drinksInWareHouse.getDescription())
//                .name(drinksInWareHouse.getName())
//                .localDateTime(drinksInWareHouse.getLocalDateTime().toString())
//                .branch(drinksInWareHouse.getBranch())
//                .warehouse(drinksInWareHouse.getWarehouse())
//                .build();
//    }
//
//    public static List<DrinkResponse> toAllResponse(List<DrinksInWareHouse> drinksInWareHouses) {
//        List<DrinkResponse> drinkResponses = new ArrayList<>();
//        drinksInWareHouses.forEach(drink -> {
//            drinkResponses.add(toResponse(drink));
//        });
//        return drinkResponses;
//    }
//}
