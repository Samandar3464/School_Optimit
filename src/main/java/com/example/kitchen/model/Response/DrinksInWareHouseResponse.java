package com.example.kitchen.model.Response;

import com.example.entity.Branch;
import com.example.kitchen.entity.Warehouse;
import lombok.*;

@Data
public class DrinksInWareHouseResponse {

    private Integer id;

    private String name;

    private int count;

    private double totalPrice;

    private int literQuantity;

    private boolean active;

    private Branch branch;

    private Warehouse warehouse;

}
