package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.enums.MeasurementType;
import com.example.kitchen.model.request.DailyConsumedProductsRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DailyConsumedProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private boolean active;

    private double quantity;

    private String description;

    private LocalDateTime localDateTime;

    private MeasurementType measurementType;

    @ManyToOne
    private User employee;

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Warehouse warehouse;

    public static DailyConsumedProducts toEntity(DailyConsumedProductsRequest request){
        return DailyConsumedProducts
                .builder()
                .name(request.getName())
                .active(true)
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .localDateTime(LocalDateTime.now())
                .measurementType(request.getMeasurementType())
                .build();
    }
}
