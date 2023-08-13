package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.TransactionHistory;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.repository.MainBalanceRepository;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class TransactionHistoryResponse {

    private Integer id;

    private double moneyAmount;

    private String accountNumber;

    private String comment;

    private String date;

    private boolean active;

    private boolean paidInFull;

    private ExpenseType expenseType;

    private PaymentType paymentType;

    private UserResponseDto taker;

    private StudentResponse student;

    private MainBalanceResponse mainBalanceResponse;

    private Branch branch;

    public static TransactionHistoryResponse toResponse(TransactionHistory transactionHistory) {
        return TransactionHistoryResponse
                .builder()
                .id(transactionHistory.getId())
                .moneyAmount(transactionHistory.getMoneyAmount())
                .expenseType(transactionHistory.getExpenseType())
                .active(transactionHistory.isActive())
                .accountNumber(transactionHistory.getStudent() == null ? null : transactionHistory.getStudent().getAccountNumber())
                .paidInFull(transactionHistory.isPaidInFull())
                .paymentType(transactionHistory.getPaymentType())
                .branch(transactionHistory.getBranch() == null ? null : transactionHistory.getBranch())
                .mainBalanceResponse(transactionHistory.getMainBalance() == null ? null : MainBalanceResponse.toResponse(transactionHistory.getMainBalance()))
                .taker(transactionHistory.getTaker() == null ? null : UserResponseDto.from(transactionHistory.getTaker()))
                .student(transactionHistory.getStudent() == null ? null : StudentResponse.from(transactionHistory.getStudent()))
                .comment(transactionHistory.getComment())
                .date(transactionHistory.getDate() == null ? null : transactionHistory.getDate().toString())
                .build();
    }

    public static List<TransactionHistoryResponse> toAllResponse(List<TransactionHistory> transactionHistories) {
        List<TransactionHistoryResponse> transactionHistoryResponses = new ArrayList<>();
        transactionHistories.forEach(transactionHistory -> {
            transactionHistoryResponses.add(toResponse(transactionHistory));
        });
        return transactionHistoryResponses;
    }
}
