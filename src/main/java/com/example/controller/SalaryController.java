package com.example.controller;

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
    public ApiResponse save(@RequestBody SalaryRequest salaryRequest){
        return salaryService.create(salaryRequest);
    }

    @GetMapping("giveCashAdvance/{salaryId}/{cashSalary}")
    public ApiResponse giveCashAdvance(@PathVariable Integer salaryId,
                                       @PathVariable double cashSalary){
        return salaryService.giveCashAdvance(salaryId,cashSalary);
    }

    @GetMapping("givePartlySalary/{salaryId}/{partlySalary}")
    public ApiResponse givePartlySalary(@PathVariable Integer salaryId,
                                        @PathVariable double partlySalary){
        return salaryService.givePartlySalary(salaryId,partlySalary);
    }

    @GetMapping("getCurrentMonthFixSalary/{fromDate}/{toDate}/{salaryId}")
    public ApiResponse getCurrentMonthFixSalary(@PathVariable String fromDate,
                                          @PathVariable String toDate,
                                          @PathVariable Integer salaryId){
        return salaryService.getCurrentMonthFixSalary(fromDate, toDate, salaryId);
    }

    @GetMapping("giveRemainSalary/{salaryId}/{salary}")
    public ApiResponse giveRemainSalary(@PathVariable Integer salaryId,
                                        @PathVariable double salary){
        return salaryService.giveRemainSalary(salaryId,salary);
    }

    @GetMapping("currentMonthSalaryAmount/{salaryId}")
    public ApiResponse giveRemainSalary(@PathVariable Integer salaryId){
        return salaryService.currentMonthSalaryAmount(salaryId);
    }

    @GetMapping("giveSalary/{salaryId}/{salary}")
    public ApiResponse giveSalary(@PathVariable Integer salaryId,
                                  @PathVariable double salary){
        return salaryService.giveSalary(salaryId,salary);
    }

    @GetMapping("getCurrentMonthTeachingHoursSalary/{salaryId}")
    public ApiResponse getCurrentMonthTeachingHoursSalary(@PathVariable Integer salaryId){
        return salaryService.getCurrentMonthTeachingHoursSalary(salaryId);
    }

    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id){
        return salaryService.getById(id);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody SalaryRequest salaryRequest){
        return salaryService.update(salaryRequest);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id){
        return salaryService.delete(id);
    }
}
