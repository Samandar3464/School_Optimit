package com.example.controller;

import com.example.entity.StudentClass;
import com.example.model.common.ApiResponse;
import com.example.servise.StudentClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/class")
public class StudentClassController {

    private final StudentClassService service;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody StudentClass studentClass){
        return service.create(studentClass);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id){
        return service.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody StudentClass studentClass){
        return service.update(studentClass);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id){
        return service.delete(id);
    }
}
