package com.example.model.response;

import com.example.entity.MainBalance;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainBalanceResponse {

    private Integer id;

    private double balance;

    private double cashBalance;

    private double plasticBalance;

    private String date;

    private Integer branchId;

    public static MainBalanceResponse toResponse(MainBalance mainBalance){
        return MainBalanceResponse
                .builder()
                .id(mainBalance.getId())
                .balance(mainBalance.getBalance())
                .cashBalance(mainBalance.getCashBalance())
                .plasticBalance(mainBalance.getPlasticBalance())
                .date(mainBalance.getDate().toString())
                .branchId(mainBalance.getBranch().getId())
                .build();
    }
}
