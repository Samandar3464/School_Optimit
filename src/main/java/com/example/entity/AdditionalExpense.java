package com.example.entity;

import com.example.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AdditionalExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private double summa;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    @OneToOne
    private User taker;

    @OneToOne
    private PaymentType paymentType;

    @JsonIgnore
    @OneToOne
    private Branch branch;

    private LocalDate givenDate;

    private LocalDateTime createdTime;
}
