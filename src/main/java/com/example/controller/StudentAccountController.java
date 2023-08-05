package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.StudentAccountCreate;
import com.example.model.request.StudentAccountRequest;
import com.example.service.StudentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studentAccount")
public class StudentAccountController {

    private final StudentAccountService studentAccountService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody StudentAccountCreate studentAccountCreate) {
        return studentAccountService.create(studentAccountCreate);
    }

    @GetMapping("/getById/{accountNumber}")
    public ApiResponse getById(@PathVariable Integer accountNumber) {
        return studentAccountService.getById(accountNumber);
    }

    @PostMapping("/payment")
    public ApiResponse payment(@RequestBody StudentAccountRequest studentAccountRequest) {
        return studentAccountService.payment(studentAccountRequest);
    }

    @PutMapping("/updatePayment")
    public ApiResponse updatePayment(@RequestBody StudentAccountRequest studentAccountRequest) {
        return studentAccountService.updatePayment(studentAccountRequest);
    }

    @GetMapping("/getByBranchId/{branchId}")
    public ApiResponse getByBranchId(@PathVariable Integer branchId) {
        return studentAccountService.getByBranchId(branchId);
    }

    @GetMapping("/getAllByDebtActive")
    public ApiResponse getAllByDebtActive() {
        return studentAccountService.getAllByDebtActive();
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody StudentAccountCreate studentAccountRequest) {
        return studentAccountService.update(studentAccountRequest);
    }

    @DeleteMapping("/delete/{accountNumber}")
    public ApiResponse delete(@PathVariable Integer accountNumber) {
        return studentAccountService.delete(accountNumber);
    }
}
