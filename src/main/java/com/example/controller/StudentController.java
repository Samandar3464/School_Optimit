package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.StudentDto;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService service;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody StudentDto studentDto) {
        return service.create(studentDto);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody StudentDto studentDto) {
        return service.update(studentDto);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }

    @GetMapping("/getAll")
    public ApiResponse getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "5") int size) {
        return service.getList(page, size);
    }

    @GetMapping("/getAllByClassName/{id}")
    public ApiResponse getAllByClassName(@PathVariable Integer id) {
        return service.getListByClassNumber(id);
    }
}
