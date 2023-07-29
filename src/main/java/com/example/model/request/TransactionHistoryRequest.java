package com.example.model.request;

import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionHistoryRequest {

    private Integer id;

    @Min(value = 1,message = "must be money Amount")
    private double moneyAmount;

    @NotBlank(message = "must be comment")
    private String comment;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private Integer takerId;

    @Min(value = 1,message = "must be mainBalanceId")
    private Integer mainBalanceId;

    @Min(value = 1,message = "must be branchId")
    private Integer branchId;
}
