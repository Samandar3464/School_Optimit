package com.example.kitchen.controller;

import com.example.kitchen.service.DailyProductsService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dailyProducts/")
@RequiredArgsConstructor
public class DailyProductsController implements BaseController<DailyProductsRequest,Integer>{

    private final DailyProductsService dailyProductsService;

    @Override
    @PostMapping("create")
    public ApiResponse create(@RequestBody DailyProductsRequest dailyProductsRequest) {
        return dailyProductsService.create(dailyProductsRequest);
    }

    @Override
    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return dailyProductsService.getById(id);
    }

    @Override
    @PutMapping("update")
    public ApiResponse update(@RequestBody DailyProductsRequest dailyProductsRequest) {
        return dailyProductsService.update(dailyProductsRequest);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return dailyProductsService.delete(id);
    }
}
