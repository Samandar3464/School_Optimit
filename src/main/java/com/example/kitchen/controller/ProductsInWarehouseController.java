package com.example.kitchen.controller;

import com.example.kitchen.service.ProductsInWareHouseService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productsInWarehouse")
public class ProductsInWarehouseController {

    private final ProductsInWareHouseService productsInWareHouseService;

    @GetMapping("{drinksInWareHouseId}")
    public ApiResponse getById(@PathVariable Integer drinksInWareHouseId) {
        return productsInWareHouseService.findByIdAndActiveTrue(drinksInWareHouseId);
    }

    @GetMapping("getAllByWarehouseId/{warehouseId}")
    public ApiResponse getAllProductsInWarehouseByWarehouseId(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "5") int size,
                                           @PathVariable Integer warehouseId) {
        return productsInWareHouseService.findAllByWarehouseIdAndActiveTrue(warehouseId, page, size);
    }

    @GetMapping("getAllByBranchId/{branchId}")
    public ApiResponse getAllByBranchIdId(@RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "5") int size,
                                          @PathVariable Integer branchId) {
        return productsInWareHouseService.getAllByBranchId(branchId, page, size);
    }
}
