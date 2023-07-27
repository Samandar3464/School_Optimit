package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.service.TeachingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teachingHours/")
public class TeachingHoursController {

    private final TeachingHoursService teachingHoursService;

    @PostMapping("/save")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAnyAuthority('SAVE_TARIFF')")
    public ApiResponse save(@RequestBody TeachingHoursRequest teachingHoursRequest) {
        return teachingHoursService.create(teachingHoursRequest);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return teachingHoursService.getById(id);
    }

    @GetMapping("/getByTeacherIdAndDate/{id}/{startDay}/{finishDay}")
    public ApiResponse getByTeacherIdAndDate(@PathVariable Integer id,
                                             @PathVariable LocalDate startDay,
                                             @PathVariable LocalDate finishDay
    ) {
        return teachingHoursService.getByTeacherIdAndDate(id, startDay,finishDay);
    }

    @GetMapping("/getByTeacherId/{id}")
    public ApiResponse getByTeacherId(@PathVariable Integer id) {
        return teachingHoursService.getByTeacherIdAndActiveTrue(id);
    }

    @GetMapping("/getAll")
    public ApiResponse getAll() {
        return teachingHoursService.getAll();
    }

    @PutMapping("/update")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAnyAuthority('UPDATE_TARIFF')")
    public ApiResponse update(@RequestBody TeachingHoursRequest teachingHoursRequest) {
        return teachingHoursService.update(teachingHoursRequest);
    }

    @DeleteMapping("/remove/{id}")
//    @PreAuthorize("hasAnyRole('SUPER_ADMIN') or hasAnyAuthority('REMOVE_TARIFF')")
    public ApiResponse remove(@PathVariable Integer id) {
        return teachingHoursService.delete(id);
    }
}
