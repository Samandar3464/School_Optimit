package com.example.kitchen.controller;

import com.example.kitchen.service.DailyDrinksService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dailyDrinks/")
@RequiredArgsConstructor
public class DailyDrinksController implements BaseController<DailyDrinksRequest,Integer>{

    private  final DailyDrinksService dailyDrinksService;

    @Override
    @PostMapping("save")
    public ApiResponse create(@RequestBody DailyDrinksRequest dailyDrinksRequest) {
        return dailyDrinksService.create(dailyDrinksRequest);
    }

    @Override
    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return dailyDrinksService.getById(id);
    }

    @GetMapping("getAllByBranchId/{id}")
    public ApiResponse getAllByBranchId(@PathVariable Integer id) {
        return dailyDrinksService.getAllByBranchId(id);
    }

    @GetMapping("getAllByWareHouseId/{id}")
    public ApiResponse getAllByWareHouseId(@PathVariable Integer id) {
        return dailyDrinksService.getAllByWareHouseId(id);
    }

    @Override
    @PutMapping("update")
    public ApiResponse update(@RequestBody DailyDrinksRequest dailyDrinksRequest) {
        return dailyDrinksService.update(dailyDrinksRequest);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return dailyDrinksService.delete(id);
    }
}
