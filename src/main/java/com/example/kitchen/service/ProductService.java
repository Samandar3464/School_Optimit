package com.example.kitchen.service;

import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements BaseService<ProductRequest,Integer>{
    @Override
    public ApiResponse create(ProductRequest productRequest) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(ProductRequest productRequest) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
