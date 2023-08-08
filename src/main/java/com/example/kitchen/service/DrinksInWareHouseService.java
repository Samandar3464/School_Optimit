package com.example.kitchen.service;

import com.example.kitchen.model.request.DrinksInWareHouseRequest;
import com.example.kitchen.repository.DrinksInWareHouseRepository;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrinksInWareHouseService implements BaseService<DrinksInWareHouseRequest,Integer>{

    private final DrinksInWareHouseRepository drinksInWareHouseRepository;

    @Override
    public ApiResponse create(DrinksInWareHouseRequest request) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DrinksInWareHouseRequest request) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
