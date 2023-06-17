package com.example.entity;

import com.example.enums.Months;
import com.example.model.request.SalaryRequest;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;

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

    private LocalDate date;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private Months month;

    private Integer userId;

    private double fix;
    //fix bu kelishilgan oylik

    private double currentMonthSalary;
    // currentMonthSalary bu ishlagan oy uchun chiqgan maoshi
    // ya'ni 30 kundan 28 kun ishlagan bolishi mumkin

    private double partlySalary;
    // bu qisman oylik yani bu oy uchun 10mln olishi kk lekin 3mln qismi chiqarib berildi

    private double givenSalary;
    // bu berilgan summa

    private double remainingSalary;
    // bu qolgan  summa yani 10mlndan 7mln qolgan bolsa shunisi

    private double cashAdvance;
    // bu naqd shaklida pul kerak bolib qolganda olgan puli

    public static Salary toSalary(SalaryRequest salaryRequest){
        return Salary
                .builder()
                .fix(salaryRequest.getFix())
//                .remainingSalary(salaryRequest.getFix())
                .currentMonthSalary(salaryRequest.getFix())
                .month(salaryRequest.getMonth())
                .active(true)
                .userId(salaryRequest.getUserId())
                .build();
    }
}
