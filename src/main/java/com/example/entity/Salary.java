package com.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double currentSumma;

    private double currentMonthSalary; // bir oylik uchun oylik

    private double givenSalary;// bu olgan summasi

    @ManyToOne
    @JsonIgnore
    private User user;

//    @JsonSerialize(using = LocalDateSerializer.class)
//    private LocalDate salaryDate;

    private Month months;

    private Integer year;

    private boolean active;

    private int staffUser; // kelmagan kunlari

}