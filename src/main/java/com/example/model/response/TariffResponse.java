package com.example.model.response;

import com.example.entity.Permission;
import com.example.entity.Tariff;
import com.example.enums.Lifetime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TariffResponse {
    private Integer id;

    private String name;

    private String description;

    private int branchAmount;

    private long productAmount;

    private int employeeAmount;

    private long tradeAmount;

    private Lifetime lifetime;

    private int testDay;

    private int interval;

    private double price;

    private double discount;

    private boolean isActive;

    private boolean isDelete;

    private List<Permission> permissionsList;


    public static TariffResponse toResponse(Tariff tariff) {
            return TariffResponse
                    .builder()
                    .id(tariff.getId())
                    .name(tariff.getName())
                    .description(tariff.getDescription())
                    .branchAmount(tariff.getBranchAmount())
                    .productAmount(tariff.getProductAmount())
                    .employeeAmount(tariff.getEmployeeAmount())
                    .tradeAmount(tariff.getTradeAmount())
                    .lifetime(tariff.getLifetime())
                    .permissionsList(tariff.getPermissions())
                    .testDay(tariff.getTestDay())
                    .interval(tariff.getInterval())
                    .price(tariff.getPrice())
                    .discount(tariff.getDiscount())
                    .isActive(tariff.isActive())
                    .isDelete(tariff.isDelete())
                    .build();
    }
}
