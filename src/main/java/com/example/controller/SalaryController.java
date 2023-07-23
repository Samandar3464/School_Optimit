package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.ExpenseDto;
import com.example.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/salary/")
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping("getTeacherSalary")
    public ApiResponse save(@RequestBody ExpenseDto expenseDto) {
        return salaryService.calculateUserSalary(expenseDto);
    }

}
