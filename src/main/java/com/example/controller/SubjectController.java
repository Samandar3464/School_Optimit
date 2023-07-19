package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequestDto;
import com.example.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subject/")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("create")
    public ApiResponse save(@RequestBody SubjectRequestDto subjectRequestDto) {
        return subjectService.create(subjectRequestDto);
    }

    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return subjectService.getById(id);
    }


    @PutMapping("update")
    public ApiResponse update(@RequestBody SubjectRequestDto subjectRequestDto) {
        return subjectService.update(subjectRequestDto);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return subjectService.delete(id);
    }

}
