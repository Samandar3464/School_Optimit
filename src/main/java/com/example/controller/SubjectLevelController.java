//package com.example.controller;
//
//import com.example.model.common.ApiResponse;
//import com.example.model.request.SubjectLevelDto;
//import com.example.service.SubjectLevelService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/subjectLevel/")
//public class SubjectLevelController {
//
//    private final SubjectLevelService subjectLevelService;
//
//    @PostMapping("create")
//    public ApiResponse save(@RequestBody SubjectLevelDto subjectLevelDto) {
//        return subjectLevelService.create(subjectLevelDto);
//    }
//
//    @GetMapping("getById/{id}")
//    public ApiResponse getById(@PathVariable Integer id) {
//        return subjectLevelService.getById(id);
//    }
//
//
//    @GetMapping("getAllByBranchId/{id}")
//    public ApiResponse getAllByBranchId(@PathVariable Integer id) {
//        return subjectLevelService.getAllByBranchId(id);
//    }
//
//
//    @PutMapping("update")
//    public ApiResponse update(@RequestBody SubjectLevelDto subjectLevelDto) {
//        return subjectLevelService.update(subjectLevelDto);
//    }
//
//    @DeleteMapping("delete/{id}")
//    public ApiResponse delete(@PathVariable Integer id) {
//        return subjectLevelService.delete(id);
//    }
//
//}
