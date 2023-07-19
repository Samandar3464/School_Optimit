package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.StaffAttendanceDto;
import com.example.service.StaffAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staffAttendance/")
public class StaffAttendanceController {

    private final StaffAttendanceService service;

    @PostMapping("save")
    public ApiResponse save(@RequestBody StaffAttendanceDto staffAttendanceDto) {
        return service.create(staffAttendanceDto);
    }

    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("getAllByBranchId")
    public ApiResponse getAllByBranchId(
            @RequestParam("brachId") Integer branchId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return service.getAllBranchId(branchId, startDate, endDate);
    }

    @GetMapping("getAllByUserId")
    public ApiResponse getAllByUserId(
            @RequestParam("userId") Integer userId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return service.getAllByUserId(userId, startDate, endDate);
    }

    @PutMapping("update")
    public ApiResponse update(@RequestBody StaffAttendanceDto staffAttendanceDto) {
        return service.update(staffAttendanceDto);
    }

    @GetMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }
}
