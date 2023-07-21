package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.ExpenseDto;
import com.example.model.request.ExpenseRequestDto;
import com.example.service.ExpenseForStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/expenseForStudent")
public class ExpenseForStudentController {

    private final ExpenseForStudentService expenseForStudentService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ExpenseRequestDto dto) {
        return expenseForStudentService.create(dto);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody ExpenseRequestDto dto) {
        return expenseForStudentService.update(dto);
    }

    @GetMapping("/getAllByBranchId")
    public ApiResponse getAllByBranchId(@RequestBody ExpenseDto expenseDto) {
        return expenseForStudentService.getAllByBranchId(expenseDto);
    }
}
