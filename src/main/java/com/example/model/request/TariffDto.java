package com.example.model.request;

import com.example.enums.Lifetime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {

    private Integer id;
    private String name;

    private String description;

    private int branchAmount;

    private int productAmount;

    private int employeeAmount;

    private Lifetime lifetime;

    private int testDay;

    private int interval;

    private double price;

    private double discount;

    private boolean active;

    private boolean delete;

    private List<Integer> permissionsList;
}
