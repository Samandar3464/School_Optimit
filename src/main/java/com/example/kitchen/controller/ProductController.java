package com.example.kitchen.controller;

import com.example.kitchen.model.request.ProductRequest;
import com.example.kitchen.service.ProductService;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product/")
@RequiredArgsConstructor
public class ProductController implements BaseController<ProductRequest,Integer>{

    private final ProductService productService;
    @Override
    @PostMapping("create")
    public ApiResponse create(@RequestBody ProductRequest productRequest) {
        return productService.create(productRequest);
    }

    @Override
    @GetMapping("getById/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    @Override
    @PutMapping("update")
    public ApiResponse update(@RequestBody ProductRequest productRequest) {
        return productService.update(productRequest);
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ApiResponse delete(@PathVariable Integer id) {
        return productService.delete(id);
    }
}
