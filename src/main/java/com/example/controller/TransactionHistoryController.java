package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.TransactionHistoryRequest;
import com.example.service.TransactionHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactionHistory")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @PostMapping("/save")
    public ApiResponse save(@RequestBody @Valid TransactionHistoryRequest transactionHistoryRequest){
       return transactionHistoryService.create(transactionHistoryRequest);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id){
        return transactionHistoryService.getById(id);
    }

    @GetMapping("/findAllByBranch_IdAndActiveTrue/{branchId}")
    public ApiResponse findAllByBranch_IdAndActiveTrue(@PathVariable Integer branchId){
        return transactionHistoryService.findAllByBranch_IdAndActiveTrue(branchId);
    }

    @GetMapping("/findAllByActiveTrue")
    public ApiResponse findAllByActiveTrue(){
        return transactionHistoryService.findAllByActiveTrue();
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody @Valid TransactionHistoryRequest transactionHistoryRequest){
        return transactionHistoryService.update(transactionHistoryRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse update(@PathVariable Integer id){
        return transactionHistoryService.delete(id);
    }
}
