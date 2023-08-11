package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DailyConsumedProducts;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.DailyConsumedProductsResponse;
import com.example.kitchen.model.request.DailyConsumedProductsRequest;
import com.example.kitchen.repository.DailyConsumedProductsRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyConsumedProductsService implements BaseService<DailyConsumedProductsRequest, Integer> {

    private final DailyConsumedProductsRepository dailyConsumedProductsRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final UserRepository userRepository;
    private final ProductsInWareHouseService productsInWareHouseService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = {RecordNotFoundException.class, Exception.class})
    public ApiResponse create(DailyConsumedProductsRequest request) {
        productsInWareHouseService.consumedProducts(request);
        DailyConsumedProducts consumedProducts =
                modelMapper.map(request, DailyConsumedProducts.class);
        setConsumedProducts(request, consumedProducts);
        dailyConsumedProductsRepository.save(consumedProducts);
        DailyConsumedProductsResponse response =
                modelMapper.map(consumedProducts, DailyConsumedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private void setConsumedProducts(DailyConsumedProductsRequest request, DailyConsumedProducts consumedProducts) {
        User user = userRepository.findByIdAndBlockedFalse(request.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        Warehouse warehouse = wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));

        consumedProducts.setActive(true);
        consumedProducts.setBranch(branch);
        consumedProducts.setEmployee(user);
        consumedProducts.setWarehouse(warehouse);
        consumedProducts.setLocalDateTime(LocalDateTime.now());
    }

    @Override
    public ApiResponse getById(Integer integer) {
        DailyConsumedProducts dailyConsumedProducts = getByID(integer);
        DailyConsumedProductsResponse response =
                modelMapper.map(dailyConsumedProducts, DailyConsumedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private DailyConsumedProducts getByID(Integer integer) {
        return dailyConsumedProductsRepository
                .findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.CONSUMED_PRODUCTS_NOT_FOUND));
    }

    @Override
    @Transactional(rollbackFor = {RecordNotFoundException.class, Exception.class})
    public ApiResponse update(DailyConsumedProductsRequest request) {
        DailyConsumedProducts old = getByID(request.getId());
        productsInWareHouseService.rollBackConsumedProducts(old);
        productsInWareHouseService.consumedProducts(request);

        DailyConsumedProducts consumedProducts =
                modelMapper.map(request, DailyConsumedProducts.class);
        setConsumedProducts(request, consumedProducts);
        consumedProducts.setId(request.getId());
        dailyConsumedProductsRepository.save(consumedProducts);
        DailyConsumedProductsResponse response =
                modelMapper.map(consumedProducts, DailyConsumedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        DailyConsumedProducts dailyConsumedProducts = getByID(integer);
        productsInWareHouseService.rollBackConsumedProducts(dailyConsumedProducts);
        dailyConsumedProducts.setActive(false);
        dailyConsumedProductsRepository.save(dailyConsumedProducts);
        DailyConsumedProductsResponse response =
                modelMapper.map(dailyConsumedProducts, DailyConsumedProductsResponse.class);
        return new ApiResponse(Constants.DELETED, true, response);
    }

    public ApiResponse getAllByWarehouseId(Integer warehouseId, int page, int size) {
        List<DailyConsumedProductsResponse> responses = new ArrayList<>();
        Page<DailyConsumedProducts> all = dailyConsumedProductsRepository
                .findAllByWarehouseIdAndActiveTrue(warehouseId, PageRequest.of(page, size));
        all.map(dailyConsumedProducts ->
                responses.add(modelMapper.map(dailyConsumedProducts, DailyConsumedProductsResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        List<DailyConsumedProductsResponse> responses = new ArrayList<>();
        Page<DailyConsumedProducts> all = dailyConsumedProductsRepository
                .findAllByBranchIdAndActiveTrue(branchId, PageRequest.of(page, size));
        all.map(dailyConsumedProducts ->
                responses.add(modelMapper.map(dailyConsumedProducts, DailyConsumedProductsResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }
}
