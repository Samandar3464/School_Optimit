package com.example.kitchen.service;

import com.example.kitchen.model.request.WareHouseRequest;
import com.example.model.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WareHouseService implements BaseService<WareHouseRequest,Integer>{
    @Override
    public ApiResponse create(WareHouseRequest wareHouseRequest) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(WareHouseRequest wareHouseRequest) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
