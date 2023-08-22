package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectLevelRequest;
import com.example.service.SubjectLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subjectLevels")
public class SubjectLevelController {

    private final SubjectLevelService subjectLevelService;

    @PostMapping
    public ApiResponse save(@RequestBody SubjectLevelRequest subjectLevelRequest) {
        return subjectLevelService.create(subjectLevelRequest);
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return subjectLevelService.getById(id);
    }


    @GetMapping("getAllByBranchId/{id}")
    public ApiResponse getAllByBranchId(@PathVariable Integer id) {
        return subjectLevelService.getAllSubjectByBranchId(id);
    }

    @PutMapping
    public ApiResponse update(@RequestBody SubjectLevelRequest subjectLevelRequest) {
        return subjectLevelService.update(subjectLevelRequest);
    }

    @DeleteMapping("{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return subjectLevelService.delete(id);
    }
}
