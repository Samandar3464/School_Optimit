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


    @PostMapping("giveCashAdvance/{phoneNumber}/{cashSalary}")
    public ApiResponse giveCashAdvance(@PathVariable String phoneNumber,
                                       @PathVariable double cashSalary,
                                       @RequestParam PaymentType paymentType) {
        return salaryService.giveCashAdvance(phoneNumber, cashSalary, paymentType);
    }

    @PostMapping("giveDebtToEmployee/{phoneNumber}/{debitAmount}")
    public ApiResponse giveDebtToEmployee(@PathVariable String phoneNumber,
                                @PathVariable double debitAmount,
                                @RequestParam PaymentType paymentType) {
        return salaryService.giveDebtToEmployee(phoneNumber, debitAmount, paymentType);
    }

    @PostMapping("debtRepayment/{phoneNumber}")
    public ApiResponse debtRepayment(@PathVariable String phoneNumber) {
        return salaryService.debtRepayment(phoneNumber);
    }

    @PostMapping("givePartlySalary/{phoneNumber}/{partlySalary}")
    public ApiResponse givePartlySalary(@PathVariable String phoneNumber,
                                        @PathVariable double partlySalary,
                                        @RequestParam PaymentType paymentType) {
        return salaryService.givePartlySalary(phoneNumber, partlySalary, paymentType);
    }

    @PostMapping("giveSalary/{phoneNumber}/{withholdingOfDebtIfAny}")
    public ApiResponse giveSalary(@PathVariable String phoneNumber,
                                  @PathVariable boolean withholdingOfDebtIfAny,
                                  @RequestParam PaymentType paymentType) {
        return salaryService.giveSalary(phoneNumber, withholdingOfDebtIfAny, paymentType);
    }

    @GetMapping("getAllByBranchId/{branchId}")
    public ApiResponse getAllByBranchId(@PathVariable Integer branchId){
        return salaryService.getAllByBranchId(branchId);
    }

    @GetMapping("getAllGivenPartlySalaryByBranchId/{branchId}")
    public ApiResponse getAllGivenPartlySalaryByBranchId(@PathVariable Integer branchId){
        return salaryService.getAllGivenPartlySalaryByBranchId(branchId);
    }
}
