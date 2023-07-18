package com.example.entity;

import com.example.enums.Lifetime;
import com.example.model.request.TariffDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false , columnDefinition="TEXT")
    private String description;

    @ManyToMany
    private List<Permission> permissions;

    private int branchAmount;

    private int productAmount;

    private int employeeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Lifetime lifetime;

    private int testDay;

    private int interval;

    @Column(nullable = false)
    private double price;

    private double discount;

    private boolean active;

    private boolean delete;

    public static Tariff toEntity(TariffDto tariffDto, List<Permission> permissions) {
        return Tariff
                .builder()
                .name(tariffDto.getName())
                .description(tariffDto.getDescription())
                .branchAmount(tariffDto.getBranchAmount())
                .productAmount(tariffDto.getProductAmount())
                .employeeAmount(tariffDto.getEmployeeAmount())
                .testDay(tariffDto.getTestDay())
                .interval(tariffDto.getInterval())
                .price(tariffDto.getPrice())
                .discount(tariffDto.getDiscount())
                .active(true)
                .delete(false)
                .lifetime(tariffDto.getLifetime())
                .permissions(permissions)
                .build();
    }
}
