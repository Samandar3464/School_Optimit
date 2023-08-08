package com.example.kitchen.service;

import com.example.kitchen.model.request.DailyConsumedDrinksRequest;
import com.example.kitchen.repository.DailyConsumedDrinksRepository;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyConsumedDrinksService implements BaseService<DailyConsumedDrinksRequest, Integer> {

    private final DailyConsumedDrinksRepository dailyConsumedDrinksRepository;

    @Override
    public ApiResponse create(DailyConsumedDrinksRequest request) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DailyConsumedDrinksRequest request) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
