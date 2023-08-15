package com.example.controller;

import com.example.model.common.ApiResponse;
import com.example.model.request.OverallReportRequest;
import com.example.service.OverallReportService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/overallReport")
public class OverallReportController {

    private final OverallReportService overallReportService;

    @PostMapping("/save")
    public ApiResponse save(@RequestBody OverallReportRequest overallReportRequest) {
        return overallReportService.create(overallReportRequest);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return overallReportService.getById(id);
    }


    @GetMapping("/getAllByDate")
    public ApiResponse getAllByDate(@RequestParam
                                    @JsonSerialize(using = LocalDateSerializer.class)
                                    @JsonDeserialize(using = LocalDateDeserializer.class)
                                    LocalDate localDate,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        return overallReportService.getAllByDate(localDate, page, size);
    }

    @GetMapping("/getAllByBranchId/{branchId}")
    public ApiResponse getAllByDate(
            @PathVariable Integer branchId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) {
        return overallReportService.getAllByBranchId(branchId, page, size);
    }


    @GetMapping("/update")
    public ApiResponse update(@RequestBody OverallReportRequest overallReportRequest) {
        return overallReportService.update(overallReportRequest);
    }

    @GetMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return overallReportService.delete(id);
    }
}
