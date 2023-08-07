package com.example.kitchen.service;

import com.example.kitchen.model.request.DailyProductsRequest;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyProductsService implements BaseService<DailyProductsRequest,Integer>{

    @Override
    public ApiResponse create(DailyProductsRequest dailyProductsRequest) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DailyProductsRequest dailyProductsRequest) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
