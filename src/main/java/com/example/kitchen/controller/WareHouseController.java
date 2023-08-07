package com.example.kitchen.controller;

import com.example.kitchen.model.request.WareHouseRequest;
import com.example.kitchen.service.WareHouseService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wareHouse/")
@RequiredArgsConstructor
public class WareHouseController implements BaseController<WareHouseRequest,Integer>{

    private final WareHouseService wareHouseService;

    @Override
    @PostMapping("create")
    public ApiResponse create(@RequestBody WareHouseRequest wareHouseRequest) {
        return wareHouseService.create(wareHouseRequest);
    }

    @Override
    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return wareHouseService.getById(id);
    }

    @Override
    @PutMapping("update")
    public ApiResponse update(@RequestBody WareHouseRequest wareHouseRequest) {
        return wareHouseService.update(wareHouseRequest);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return wareHouseService.delete(id);
    }
}
