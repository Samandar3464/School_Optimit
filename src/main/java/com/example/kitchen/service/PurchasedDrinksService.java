package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DrinksInWareHouse;
import com.example.kitchen.entity.PurchasedDrinks;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.PurchasedDrinksResponse;
import com.example.kitchen.model.request.PurchasedDrinksRequest;
import com.example.kitchen.repository.PurchasedDrinksRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchasedDrinksService implements BaseService<PurchasedDrinksRequest, Integer> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final DrinksInWareHouseService drinksInWareHouseService;
    private final PurchasedDrinksRepository purchasedDrinksRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse create(PurchasedDrinksRequest request) {
        drinksInWareHouseService.storageOfPurchasedDrinks(request);
        PurchasedDrinks purchasedDrinks = modelMapper.map(request, PurchasedDrinks.class);
        setPurchasedDrinks(request, purchasedDrinks);
        purchasedDrinksRepository.save(purchasedDrinks);
        PurchasedDrinksResponse response = modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer id) {
        PurchasedDrinks purchasedDrinks = getByPurchasedDrinksId(id);
        PurchasedDrinksResponse response = modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse update(PurchasedDrinksRequest request) {
        updateWareHouse(request);
        PurchasedDrinks purchasedDrinks = modelMapper.map(request, PurchasedDrinks.class);
        purchasedDrinks.setId(request.getId());
        setPurchasedDrinks(request, purchasedDrinks);
        purchasedDrinksRepository.save(purchasedDrinks);
        PurchasedDrinksResponse response = modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private void updateWareHouse(PurchasedDrinksRequest request) {
        PurchasedDrinks oldPurchasedDrinks = getByPurchasedDrinksId(request.getId());
        DrinksInWareHouse drinksInWareHouse = drinksInWareHouseService.rollBackPurchasedDrinks(oldPurchasedDrinks);
        drinksInWareHouseService.storageOfPurchasedDrinks(request);
        checkingWarehouseIfValid(drinksInWareHouse);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse delete(Integer integer) {
        PurchasedDrinks purchasedDrinks = getByPurchasedDrinksId(integer);
        DrinksInWareHouse drinksInWareHouse = drinksInWareHouseService.rollBackPurchasedDrinks(purchasedDrinks);
        checkingWarehouseIfValid(drinksInWareHouse);
        purchasedDrinks.setActive(false);
        purchasedDrinksRepository.save(purchasedDrinks);
        PurchasedDrinksResponse response = modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class);
        return new ApiResponse(Constants.DELETED, true, response);
    }


    private void setPurchasedDrinks(PurchasedDrinksRequest request, PurchasedDrinks purchasedDrinks) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        User user = userRepository.findByIdAndBlockedFalse(request.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
        Warehouse warehouse = wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));

        purchasedDrinks.setActive(true);
        purchasedDrinks.setBranch(branch);
        purchasedDrinks.setEmployee(user);
        purchasedDrinks.setWarehouse(warehouse);
    }

    private void checkingWarehouseIfValid(DrinksInWareHouse drinksInWareHouse) {
        if (drinksInWareHouse.getCount() < 0 || drinksInWareHouse.getTotalPrice() < 0) {
            throw new RecordNotFoundException(Constants.DRINKS_IN_WAREHOUSE_NOT_ENOUGH);
        }
    }

    private PurchasedDrinks getByPurchasedDrinksId(Integer integer) {
        return purchasedDrinksRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.PURCHASED_DRINKS_NOT_FOUND));
    }

    public ApiResponse getAllByWarehouseId(Integer warehouseId, int page, int size) {
        Page<PurchasedDrinks> all = purchasedDrinksRepository.findAllByWarehouseIdAndActiveTrue(warehouseId, PageRequest.of(page, size));
        List<PurchasedDrinksResponse> responses = new ArrayList<>();
        all.map(purchasedDrinks -> responses.add(modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        Page<PurchasedDrinks> all = purchasedDrinksRepository
                .findAllByBranch_IdAndActiveTrue(branchId, PageRequest.of(page, size));
        List<PurchasedDrinksResponse> responses = new ArrayList<>();
        all.map(purchasedDrinks ->
                responses.add(modelMapper.map(purchasedDrinks, PurchasedDrinksResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }
}
