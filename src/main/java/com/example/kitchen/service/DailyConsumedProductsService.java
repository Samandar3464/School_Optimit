package com.example.kitchen.service;

import com.example.kitchen.model.request.DailyConsumedProductsRequest;
import com.example.kitchen.repository.DailyConsumedProductsRepository;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyConsumedProductsService implements BaseService<DailyConsumedProductsRequest, Integer> {

   private final DailyConsumedProductsRepository dailyConsumedProductsRepository;

    @Override
    public ApiResponse create(DailyConsumedProductsRequest request) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DailyConsumedProductsRequest request) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
