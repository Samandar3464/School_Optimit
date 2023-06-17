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

    @PostMapping("giveCashAdvance")
    public ApiResponse giveCashAdvance(@RequestBody SalaryRequest salaryRequest){
        return salaryService.giveCashAdvance(salaryRequest);
    }

    @PostMapping("givePartlySalary")
    public ApiResponse givePartlySalary(@RequestBody SalaryRequest salaryRequest){
        return salaryService.givePartlySalary(salaryRequest);
    }

    @PostMapping("getById/{id}")
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
