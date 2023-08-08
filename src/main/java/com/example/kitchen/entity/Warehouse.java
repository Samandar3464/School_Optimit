package com.example.kitchen.entity;

import com.example.entity.Branch;
import com.example.kitchen.model.request.WareHouseRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private boolean active;

    @ManyToOne
    private Branch branch;

    public static Warehouse toEntity(WareHouseRequest request){
     return Warehouse
             .builder()
             .name(request.getName())
             .active(true)
             .build();
    }
}
