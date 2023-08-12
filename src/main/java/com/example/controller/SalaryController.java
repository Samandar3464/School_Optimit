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


    @PostMapping("giveCashAdvance")
    public ApiResponse giveCashAdvance(@RequestParam String phoneNumber,
                                       @RequestParam double cashSalary,
                                       @RequestParam PaymentType paymentType) {
        return salaryService.giveCashAdvance(phoneNumber, cashSalary, paymentType);
    }

    @PostMapping("giveDebtToEmployee")
    public ApiResponse giveDebtToEmployee(@RequestParam String phoneNumber,
                                          @RequestParam double debitAmount,
                                          @RequestParam PaymentType paymentType) {
        return salaryService.giveDebtToEmployee(phoneNumber, debitAmount, paymentType);
    }

    @PostMapping("debtRepayment")
    public ApiResponse debtRepayment(@RequestParam String phoneNumber) {
        return salaryService.debtRepayment(phoneNumber);
    }

    @PostMapping("givePartlySalary")
    public ApiResponse givePartlySalary(@RequestParam String phoneNumber,
                                        @RequestParam double partlySalary,
                                        @RequestParam PaymentType paymentType) {
        return salaryService.givePartlySalary(phoneNumber, partlySalary, paymentType);
    }

    @PostMapping("giveSalary")
    public ApiResponse giveSalary(@RequestParam String phoneNumber,
                                  @RequestParam boolean withholdingOfDebtIfAny,
                                  @RequestParam PaymentType paymentType) {
        return salaryService.giveSalary(phoneNumber, withholdingOfDebtIfAny, paymentType);
    }

    @GetMapping("getAllByBranchId/{branchId}")
    public ApiResponse getAllByBranchId(@PathVariable Integer branchId) {
        return salaryService.getAllByBranchId(branchId);
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

}
