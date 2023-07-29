package com.example.entity;

import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.model.request.TransactionHistoryRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private double moneyAmount;

    private String comment;

    private LocalDateTime date;

    private boolean active;

    private Integer mainBalanceId;

    private ExpenseType expenseType;

    private PaymentType paymentType;

    @ManyToOne
    private User taker;

    @OneToOne
    private Branch branch;

    public static TransactionHistory toEntity(TransactionHistoryRequest transactionHistoryRequest){
        return TransactionHistory
                .builder()
                .active(true)
                .date(transactionHistoryRequest.getDate())
                .comment(transactionHistoryRequest.getComment())
                .paymentType(transactionHistoryRequest.getPaymentType())
                .moneyAmount(transactionHistoryRequest.getMoneyAmount())
                .expenseType(transactionHistoryRequest.getExpenseType())
                .mainBalanceId(transactionHistoryRequest.getMainBalanceId())
                .build();
    }
}
