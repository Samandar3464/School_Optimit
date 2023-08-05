package com.example.controller;

import com.example.enums.PaymentType;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/salary/")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping("save")
    public ApiResponse save(@RequestBody SalaryRequest salaryRequest) {
        return salaryService.create(salaryRequest);
    }

    @GetMapping("getByUserId/{phoneNumber}")
    public ApiResponse getByUserId(@PathVariable String phoneNumber) {
        return salaryService.getById(phoneNumber);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody SalaryRequest salaryRequest) {
        return salaryService.update(salaryRequest);
    }

    @DeleteMapping("delete/{phoneNumber}")
    public ApiResponse delete(@PathVariable String phoneNumber) {
        return salaryService.delete(phoneNumber);
    }


    @GetMapping("giveCashAdvance/{phoneNumber}/{cashSalary}")
    public ApiResponse giveCashAdvance(@PathVariable String phoneNumber,
                                       @PathVariable double cashSalary,
                                       @RequestParam PaymentType paymentType) {
        return salaryService.giveCashAdvance(phoneNumber, cashSalary, paymentType);
    }

    @GetMapping("giveDebtToEmployee/{phoneNumber}/{debitAmount}")
    public ApiResponse giveDebtToEmployee(@PathVariable String phoneNumber,
                                @PathVariable double debitAmount,
                                @RequestParam PaymentType paymentType) {
        return salaryService.giveDebtToEmployee(phoneNumber, debitAmount, paymentType);
    }

    @GetMapping("debtRepayment/{phoneNumber}")
    public ApiResponse debtRepayment(@PathVariable String phoneNumber) {
        return salaryService.debtRepayment(phoneNumber);
    }

    @GetMapping("givePartlySalary/{phoneNumber}/{partlySalary}")
    public ApiResponse givePartlySalary(@PathVariable String phoneNumber,
                                        @PathVariable double partlySalary,
                                        @RequestParam PaymentType paymentType) {
        return salaryService.givePartlySalary(phoneNumber, partlySalary, paymentType);
    }

    @GetMapping("giveSalary/{phoneNumber}/{withholdingOfDebtIfAny}")
    public ApiResponse giveSalary(@PathVariable String phoneNumber,
                                  @PathVariable boolean withholdingOfDebtIfAny,
                                  @RequestParam PaymentType paymentType) {
        return salaryService.giveSalary(phoneNumber, withholdingOfDebtIfAny, paymentType);
    }
}
