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

    @Enumerated
    private Months month;

    private double fix;
    //fix bu kelishilgan oylik

    private double currentMonthSalary;
    // currentMonthSalary bu ishlagan oy uchun chiqgan maoshi
    // ya'ni 30 kundan 28 kun ishlagan bolishi mumkin

    private double partlySalary;
    // bu qisman oylik yani bu oy uchun 10mln olishi kk lekin 3mln qismi chiqarib berildi

    private double givenSalary;
    // bu berilgan summa

    private double remainingSalary;
    // bu qolgan  summa yani 10mlndan 7mln qolgan bolsa shunisi

    private double cashAdvance;
    // bu naqd shaklida pul kerak bolib qolganda olgan puli
    private double classLeaderSalary;
    private double amountDebt;

    public static SalaryResponse toResponse(Salary salary) {
        return SalaryResponse
                .builder()
                .id(salary.getId())
                .month(salary.getMonth())
                .amountDebt(salary.getAmountDebt())
                .classLeaderSalary(salary.getClassLeaderSalary())
                .remainingSalary(salary.getRemainingSalary())
                .partlySalary(salary.getPartlySalary())
                .givenSalary(salary.getGivenSalary())
                .fix(salary.getFix())
                .cashAdvance(salary.getCashAdvance())
                .currentMonthSalary(salary.getCurrentMonthSalary())
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
