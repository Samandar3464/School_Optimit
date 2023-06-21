package com.example.controller;


import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryHoursRequest;
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

    @GetMapping("currentMonthSalary/{fromDate}/{toDate}/{id}")
    public ApiResponse currentMonthSalary(@PathVariable String fromDate,
                                          @PathVariable String toDate,
                                          @PathVariable Integer id){
        return salaryService.currentMonthSalary(fromDate, toDate, id);
    }

//    @PostMapping("giveRemainSalary")
//    public ApiResponse giveRemainSalary(@RequestBody SalaryRequest salaryRequest){
//        return salaryService.giveRemainSalary(salaryRequest);
//    }

    @PostMapping("giveSalary")
    public ApiResponse giveSalary(@RequestBody SalaryRequest salaryRequest){
        return salaryService.giveSalary(salaryRequest);
    }

    @PostMapping("getTeachingHoursSalary")
    public ApiResponse giveSalary(@RequestBody SalaryHoursRequest salaryRequest){
        return salaryService.getTeachingHoursSalary(salaryRequest);
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
