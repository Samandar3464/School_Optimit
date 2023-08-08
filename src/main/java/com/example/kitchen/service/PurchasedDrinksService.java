package com.example.kitchen.service;

import com.example.kitchen.model.request.PurchasedDrinksRequest;
import com.example.kitchen.repository.PurchasedDrinksRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchasedDrinksService implements BaseService<PurchasedDrinksRequest, Integer> {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final PurchasedDrinksRepository purchasedDrinksRepository;

    @Override
    public ApiResponse create(PurchasedDrinksRequest request) {
        return null;
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(PurchasedDrinksRequest request) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
