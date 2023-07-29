package com.example.model.response;

import com.example.entity.TransactionHistory;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class TransactionHistoryResponse {

    private Integer id;

    private double moneyAmount;

    private String comment;

    private String date;

    private boolean active;

    private ExpenseType expenseType;

    private PaymentType paymentType;

    private Integer takerId;

    private Integer branchId;

    public static TransactionHistoryResponse toResponse(TransactionHistory transactionHistory) {
        return TransactionHistoryResponse
                .builder()
                .id(transactionHistory.getId())
                .moneyAmount(transactionHistory.getMoneyAmount())
                .expenseType(transactionHistory.getExpenseType())
                .active(transactionHistory.isActive())
                .paymentType(transactionHistory.getPaymentType())
                .branchId(transactionHistory.getBranch().getId())
                .takerId(transactionHistory.getTaker() == null ? null : transactionHistory.getTaker().getId())
                .comment(transactionHistory.getComment())
                .date(transactionHistory.getDate().toString())
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
