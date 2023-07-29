package com.example.controller;

import com.example.enums.PaymentType;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.service.SalaryService;
import jakarta.persistence.Enumerated;
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


    @GetMapping("giveCashAdvance/{userId}/{cashSalary}")
    public ApiResponse giveCashAdvance(@PathVariable Integer userId,
                                       @PathVariable double cashSalary,
                                       @RequestParam PaymentType paymentType) {
        return salaryService.giveCashAdvance(userId, cashSalary, paymentType);
    }

    @GetMapping("giveDebtToEmployee/{userId}/{debitAmount}")
    public ApiResponse giveDebtToEmployee(@PathVariable Integer userId,
                                @PathVariable double debitAmount,
                                @RequestParam PaymentType paymentType) {
        return salaryService.giveDebtToEmployee(userId, debitAmount, paymentType);
    }

    @GetMapping("debtRepayment/{userId}")
    public ApiResponse debtRepayment(@PathVariable Integer userId) {
        return salaryService.debtRepayment(userId);
    }

    @GetMapping("givePartlySalary/{userId}/{partlySalary}")
    public ApiResponse givePartlySalary(@PathVariable Integer userId,
                                        @PathVariable double partlySalary,
                                        @RequestParam PaymentType paymentType) {
        return salaryService.givePartlySalary(userId, partlySalary, paymentType);
    }

    @GetMapping("giveSalary/{userId}/{withholdingOfDebtIfAny}")
    public ApiResponse giveSalary(@PathVariable Integer userId,
                                  @PathVariable boolean withholdingOfDebtIfAny,
                                  @RequestParam PaymentType paymentType) {
        return salaryService.giveSalary(userId, withholdingOfDebtIfAny, paymentType);
    }
}
