package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequest;
import com.example.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subject/")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("save")
    public ApiResponse save(@RequestBody SubjectRequest subjectRequest) {
        return subjectService.create(subjectRequest);
    }

    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return subjectService.getById(id);
    }

    @GetMapping("getAllSubjectByBranchId/{id}")
    public ApiResponse getAllSubjectByBranchId(@PathVariable Integer id) {
        return subjectService.getAllSubjectByBranchId(id);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody SubjectRequest subjectRequest) {
        return subjectService.update(subjectRequest);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return subjectService.delete(id);
    }

}
