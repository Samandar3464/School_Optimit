package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.ExpenseRequestDto;
import com.example.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody ExpenseRequestDto dto) {
        return expenseService.create(dto);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody ExpenseRequestDto dto) {
        return expenseService.update(dto);
    }

    @GetMapping("/getAllByBranchId")
    public ApiResponse getAllByBranchId(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "startDate") LocalDateTime startDate,
            @RequestParam(name = "endDate") LocalDateTime endDate
    ) {
        return expenseService.getAllByBranchId(id, startDate, endDate);
    }
}
