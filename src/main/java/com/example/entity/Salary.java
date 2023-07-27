package com.example.entity;

import com.example.model.request.SalaryRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private double fix;

    private double partlySalary;

    private double givenSalary;

    private double salary;

    private double cashAdvance;

    private double amountDebt;

    private double classLeaderSalary;

    @ManyToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public static Salary toSalary(SalaryRequest salaryRequest) {
        return Salary
                .builder()
                .fix(salaryRequest.getFix())
                .active(true)
                .date(salaryRequest.getDate())
                .givenSalary(salaryRequest.getGivenSalary())
                .partlySalary(salaryRequest.getPartlySalary())
                .salary(salaryRequest.getSalary())
                .cashAdvance(salaryRequest.getCashAdvance())
                .amountDebt(salaryRequest.getDebtAmount())
                .classLeaderSalary(salaryRequest.getClassLeaderSalary())
                .build();
    }
}