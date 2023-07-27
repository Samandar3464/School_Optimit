package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/salary/")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping("save")
    public ApiResponse save(@RequestBody SalaryRequest salaryRequest) {
        return salaryService.create(salaryRequest);
    }

    @GetMapping("getByUserId/{userId}")
    public ApiResponse getByUserId(@PathVariable Integer userId) {
        return salaryService.getById(userId);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody SalaryRequest salaryRequest) {
        return salaryService.update(salaryRequest);
    }

    @DeleteMapping("delete/{userId}")
    public ApiResponse delete(@PathVariable Integer userId) {
        return salaryService.delete(userId);
    }


    @GetMapping("giveCashAdvance/{userId}/{cashSalary}/{paymentTypeId}")
    public ApiResponse giveCashAdvance(@PathVariable Integer userId,
                                       @PathVariable double cashSalary,
                                       @PathVariable Integer paymentTypeId) {
        return salaryService.giveCashAdvance(userId, cashSalary, paymentTypeId);
    }

    @GetMapping("giveDebtToEmployee/{userId}/{debitAmount}/{paymentTypeId}")
    public ApiResponse giveDebtToEmployee(@PathVariable Integer userId,
                                @PathVariable double debitAmount,
                                @PathVariable Integer paymentTypeId) {
        return salaryService.giveDebtToEmployee(userId, debitAmount, paymentTypeId);
    }

    @GetMapping("debtRepayment/{userId}")
    public ApiResponse debtRepayment(@PathVariable Integer userId) {
        return salaryService.debtRepayment(userId);
    }

    @GetMapping("givePartlySalary/{userId}/{partlySalary}/{paymentTypeId}")
    public ApiResponse givePartlySalary(@PathVariable Integer userId,
                                        @PathVariable double partlySalary,
                                        @PathVariable Integer paymentTypeId) {
        return salaryService.givePartlySalary(userId, partlySalary, paymentTypeId);
    }

    @GetMapping("giveSalary/{userId}/{withholdingOfDebtIfAny}/{paymentTypeId}")
    public ApiResponse giveSalary(@PathVariable Integer userId,
                                  @PathVariable boolean withholdingOfDebtIfAny,
                                  @PathVariable Integer paymentTypeId) {
        return salaryService.giveSalary(userId, withholdingOfDebtIfAny, paymentTypeId);
    }
}
