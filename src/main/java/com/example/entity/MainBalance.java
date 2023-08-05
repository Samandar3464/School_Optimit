package com.example.entity;

import com.example.model.request.MainBalanceRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class MainBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double balance;

    private double cashBalance;

    private double plasticBalance;

    private int accountNumber;

    private LocalDate date;

    private boolean active;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;


    public static MainBalance toEntity(MainBalanceRequest mainBalanceRequest) {
        return MainBalance
                .builder()
                .balance(mainBalanceRequest.getBalance())
                .plasticBalance(mainBalanceRequest.getPlasticBalance())
                .cashBalance(mainBalanceRequest.getCashBalance())
                .accountNumber(mainBalanceRequest.getAccountNumber())
                .date(LocalDate.now())
                .active(true)
                .build();
    }
}
