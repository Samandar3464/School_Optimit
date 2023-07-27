package com.example.model.response;

import com.example.entity.Salary;
import com.example.enums.Months;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SalaryResponse {

    private Integer id;

    private String date;

    private Integer userId;

    private Integer branchId;

    private double fix;

    private double partlySalary;

    private double givenSalary;

    private double salary;

    private double cashAdvance;

    private double amountDebt;

    private double classLeaderSalary;

    public static SalaryResponse toResponse(Salary salary) {
        return SalaryResponse
                .builder()
                .id(salary.getId())
                .fix(salary.getFix())
                .salary(salary.getSalary())
                .userId(salary.getUser().getId())
                .date(salary.getDate().toString())
                .amountDebt(salary.getAmountDebt())
                .givenSalary(salary.getGivenSalary())
                .cashAdvance(salary.getCashAdvance())
                .branchId(salary.getBranch().getId())
                .partlySalary(salary.getPartlySalary())
                .classLeaderSalary(salary.getClassLeaderSalary())
                .build();
    }

    public static List<SalaryResponse> toAllResponse(List<Salary> salaries) {
        List<SalaryResponse> salaryResponses = new ArrayList<>();
        salaries.forEach(salary -> {
            salaryResponses.add(SalaryResponse.toResponse(salary));
        });
        return salaryResponses;
    }
}
