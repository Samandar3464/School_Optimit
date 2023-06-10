package com.example.controller;

import com.example.entity.Family;
import com.example.model.common.ApiResponse;
import com.example.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/family")
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping("/create")
    public ApiResponse create(@RequestBody @Validated Family family) {
        return familyService.create(family);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return familyService.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody @Validated Family family) {
        return familyService.update(family);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return familyService.delete(id);
    }

    @GetMapping("/getAllActiveClasses")
    public ApiResponse getAllActiveClasses(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        return familyService.getList(page, size);
    }

}
