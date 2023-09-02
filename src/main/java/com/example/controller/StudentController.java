package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.FamilyLoginDto;
import com.example.model.request.StudentRequest;
import com.example.service.StudentService;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService service;

    @PostMapping("/create")
<<<<<<< HEAD
    public ApiResponse create(@RequestBody StudentRequest studentRequest) {
=======
    public ApiResponse create(@ModelAttribute  StudentRequest studentRequest) {
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        return service.create(studentRequest);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PutMapping("/update")
<<<<<<< HEAD
    public ApiResponse update(@RequestBody StudentRequest studentRequest) {
=======
    public ApiResponse update(@ModelAttribute StudentRequest studentRequest) {
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        return service.update(studentRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }

    @GetMapping("/getAll")
    public ApiResponse getAll(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "5") int size,
                              @RequestParam(name = "branchId") int branchId) {
        return service.getList(page, size, branchId);
    }

    @GetMapping("/getAllByClassId/{classId}/{branchId}")
    public ApiResponse getAllByClassName(@PathVariable Integer classId, @PathVariable Integer branchId) {
        return service.getListByClassNumber(classId, branchId);
    }

    @GetMapping("/getAllNeActiveStudents/{branchId}")
    public ApiResponse getAllNeActiveStudents(@PathVariable Integer branchId) {
        return service.getAllNeActiveStudents(branchId);
    }

    @PostMapping("/studentLogin")
    public ApiResponse studentLogin(@RequestBody FamilyLoginDto studentLogin) {
        return service.studentLogIn(studentLogin);
    }

<<<<<<< HEAD
    @GetMapping("/search")
    public HttpEntity<?> search(@RequestParam String name) {
        ApiResponse apiResponse = service.search(name);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }

=======
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
}
