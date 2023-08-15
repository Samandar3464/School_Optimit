package com.example.model.response;

import com.example.entity.Branch;
import lombok.Builder;
import lombok.Data;

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
}
