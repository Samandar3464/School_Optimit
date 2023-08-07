package com.example.kitchen.service;

import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DailyConsumedDrinks;
import com.example.kitchen.model.request.DailyDrinksRequest;
import com.example.kitchen.model.response.DailyDrinksResponse;
import com.example.kitchen.repository.DailyDrinksRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyDrinksService implements BaseService<DailyDrinksRequest, Integer> {

    private final DailyDrinksRepository dailyDrinksRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;

    @Override
    public ApiResponse create(DailyDrinksRequest dailyDrinksRequest) {
        DailyConsumedDrinks dailyConsumedDrinks = DailyConsumedDrinks.toEntity(dailyDrinksRequest);
        set(dailyDrinksRequest, dailyConsumedDrinks);
        dailyDrinksRepository.save(dailyConsumedDrinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toResponse(dailyConsumedDrinks));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        DailyConsumedDrinks dailyConsumedDrinks = dailyDrinksRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.DAILY_DRINKS_NOT_FOUND));
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toResponse(dailyConsumedDrinks));
    }

    public ApiResponse getAll() {
        List<DailyConsumedDrinks> all = dailyDrinksRepository.findAllByActiveTrue();
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toAllResponse(all));
    }

    public ApiResponse getAllByBranchId(Integer id) {
        List<DailyConsumedDrinks> all = dailyDrinksRepository.findAllByActiveTrueAndBranchId(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toAllResponse(all));
    }

    public ApiResponse getAllByWareHouseId(Integer id) {
        List<DailyConsumedDrinks> all = dailyDrinksRepository.findAllByActiveTrueAndWarehouseId(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toAllResponse(all));
    }


    @Override
    public ApiResponse update(DailyDrinksRequest dailyDrinksRequest) {
        dailyDrinksRepository.findByIdAndActiveTrue(dailyDrinksRequest.getId()).orElseThrow(() -> new RecordNotFoundException(Constants.DAILY_DRINKS_NOT_FOUND));
        DailyConsumedDrinks dailyConsumedDrinks = DailyConsumedDrinks.toEntity(dailyDrinksRequest);
        set(dailyDrinksRequest, dailyConsumedDrinks);
        dailyDrinksRepository.save(dailyConsumedDrinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toResponse(dailyConsumedDrinks));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        DailyConsumedDrinks dailyConsumedDrinks = dailyDrinksRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.DAILY_DRINKS_NOT_FOUND));
        dailyDrinksRepository.delete(dailyConsumedDrinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, DailyDrinksResponse.toResponse(dailyConsumedDrinks));
    }

    private void set(DailyDrinksRequest dailyDrinksRequest, DailyConsumedDrinks dailyConsumedDrinks) {
        dailyConsumedDrinks.setBranch(branchRepository.findByIdAndDeleteFalse(dailyDrinksRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        dailyConsumedDrinks.setWarehouse(wareHouseRepository.findByIdAndActiveTrue(dailyDrinksRequest.getWarehouseId()).orElseThrow(() -> new RecordNotFoundException(Constants.DAILY_DRINKS_NOT_FOUND)));
    }
}
