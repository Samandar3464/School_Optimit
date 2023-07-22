package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.service.TeachingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teachingHours")
public class TeachingHoursController {

    private final TeachingHoursService teachingHoursService;

    @PostMapping("/save")
    public ApiResponse save(@RequestBody TeachingHoursRequest teachingHoursRequest) {
        return teachingHoursService.create(teachingHoursRequest);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable UUID id) {
        return teachingHoursService.getById(id);
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody TeachingHoursRequest teachingHoursRequest) {
        return teachingHoursService.update(teachingHoursRequest);
    }

    @DeleteMapping("/remove/{id}")
    public ApiResponse remove(@PathVariable UUID id) {
        return teachingHoursService.delete(id);
    }

    @GetMapping("/getAllByTeacherId")
    public ApiResponse getByTeacherId(@RequestParam Integer id,
                                      @RequestParam LocalDate startDate,
                                      @RequestParam LocalDate endDate) {
        return teachingHoursService.getAllByTeacherId(id, startDate, endDate);
    }

}
