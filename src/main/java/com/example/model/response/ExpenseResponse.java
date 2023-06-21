package com.example.model.response;

import com.example.entity.Expense;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ExpenseResponse {


    private Integer id;

    private double summa;

    private String reason;

    private Integer takerId;

    private String takerName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;

    public static ExpenseResponse from(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .summa(expense.getSumma())
                .reason(expense.getReason())
                .takerId(expense.getTaker().getId())
                .takerName(expense.getTaker().getFullName())
                .createdTime(expense.getCreatedTime())
                .build();
    }
}
