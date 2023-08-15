package com.example.entity;

import com.example.model.request.BusinessRequest;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String phoneNumber;

    private boolean active;

    private boolean delete;

    public static Business from(BusinessRequest business){
        return Business.builder()
                .name(business.getName())
                .description(business.getDescription())
                .phoneNumber(business.getPhoneNumber())
                .active(true)
                .delete(false)
                .build();
    }
}
