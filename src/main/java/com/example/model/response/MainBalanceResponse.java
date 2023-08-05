package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.MainBalance;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MainBalanceResponse {

    private Integer id;

    private double balance;

    private double cashBalance;

    private double plasticBalance;

    private int accountNumber;

    private Branch branch;

    private String date;

    public static MainBalanceResponse toResponse(MainBalance mainBalance) {
        return MainBalanceResponse
                .builder()
                .id(mainBalance.getId())
                .balance(mainBalance.getBalance())
                .cashBalance(mainBalance.getCashBalance())
                .accountNumber(mainBalance.getAccountNumber())
                .plasticBalance(mainBalance.getPlasticBalance())
                .date(mainBalance.getDate() == null ? null : mainBalance.getDate().toString())
                .branch(mainBalance.getBranch())
                .build();
    }

    public static List<MainBalanceResponse> toAllResponse(List<MainBalance> all) {
        List<MainBalanceResponse> mainBalanceResponses = new ArrayList<>();
        all.forEach(mainBalance -> {
            mainBalanceResponses.add(toResponse(mainBalance));
        });
        return mainBalanceResponses;
    }
}
