package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.StudentAccountDto;
import com.example.service.StudentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class StudentAccountController {

    private final StudentAccountService studentAccountService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody StudentAccountDto studentAccount) {
        return studentAccountService.create(studentAccount);
    }
    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return studentAccountService.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody StudentAccountDto studentAccount) {
        return studentAccountService.update(studentAccount);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return studentAccountService.delete(id);
    }

    @GetMapping("/getByBranchId/{id}")
    public ApiResponse getByBranchId(@PathVariable Integer id) {
        return studentAccountService.getByBranchId(id);
    }

    @GetMapping("/getByStudentId/{id}")
    public ApiResponse getByStudentId(@PathVariable Integer id) {
        return studentAccountService.getByStudentId(id);
    }
}
