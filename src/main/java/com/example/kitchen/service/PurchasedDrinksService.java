package com.example.kitchen.service;

import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DrinksInWareHouse;
import com.example.kitchen.entity.PurchasedDrinks;
import com.example.kitchen.model.Response.PurchasedDrinksResponse;
import com.example.kitchen.model.request.PurchasedDrinksRequest;
import com.example.kitchen.repository.DrinksInWareHouseRepository;
import com.example.kitchen.repository.PurchasedDrinksRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchasedDrinksService implements BaseService<PurchasedDrinksRequest, Integer> {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final DrinksInWareHouseRepository drinksInWareHouseRepository;
    private final PurchasedDrinksRepository purchasedDrinksRepository;

    @Override
    public ApiResponse create(PurchasedDrinksRequest request) {
        PurchasedDrinks purchasedDrinks = PurchasedDrinks.toEntity(request);
        setPurchasedDrinks(request, purchasedDrinks);
        checkingWarehouseAndSetPurchasedDrinks(request);
        purchasedDrinksRepository.save(purchasedDrinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedDrinksResponse.toResponse(purchasedDrinks));
    }

    @Override
    public ApiResponse getById(Integer id) {
        PurchasedDrinks purchasedDrinks = findById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedDrinksResponse.toResponse(purchasedDrinks));
    }

    @Override
    @ExceptionHandler({RecordNotFoundException.class})
    public ApiResponse update(PurchasedDrinksRequest request) {
        rollBackPurchasedDrinks(findById(request.getId()));
        checkingWarehouseAndSetPurchasedDrinks(request);
        PurchasedDrinks purchasedDrinks = PurchasedDrinks.toEntity(request);
        setPurchasedDrinks(request, purchasedDrinks);
        purchasedDrinks.setId(request.getId());
        purchasedDrinksRepository.save(purchasedDrinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedDrinksResponse.toResponse(purchasedDrinks));
    }

    @Override
    @ExceptionHandler({RecordNotFoundException.class})
    public ApiResponse delete(Integer integer) {
        PurchasedDrinks purchasedDrinks = findById(integer);
        purchasedDrinks.setActive(false);
        purchasedDrinksRepository.save(purchasedDrinks);
        rollBackPurchasedDrinks(purchasedDrinks);
        return new ApiResponse(Constants.DELETED, true, PurchasedDrinksResponse.toResponse(purchasedDrinks));
    }

    private void checkingWarehouseAndSetPurchasedDrinks(PurchasedDrinksRequest request) {
        Optional<DrinksInWareHouse> drinksInWareHouse = drinksInWareHouseRepository.findByNameAndLiterQuantityAndBranchIdAndWarehouseIdAndActiveTrue(request.getName(), request.getLiterQuantity(), request.getBranchId(), request.getWarehouseId());
        if (drinksInWareHouse.isPresent()) {
            drinksInWareHouse.get().setCount(drinksInWareHouse.get().getCount() + request.getCount());
            drinksInWareHouse.get().setTotalPrice(drinksInWareHouse.get().getTotalPrice() + request.getTotalPrice());
            drinksInWareHouseRepository.save(drinksInWareHouse.get());
        }
    }

    private void rollBackPurchasedDrinks(PurchasedDrinks purchasedDrinks) {
        DrinksInWareHouse drinksInWareHouse = drinksInWareHouseRepository.findByNameAndLiterQuantityAndBranchIdAndWarehouseIdAndActiveTrue(purchasedDrinks.getName(), purchasedDrinks.getLiterQuantity(), purchasedDrinks.getBranch().getId(), purchasedDrinks.getWarehouse().getId()).orElseThrow(() -> new RecordNotFoundException(Constants.DRINKS_IN_WAREHOUSE_NOT_FOUND));
        drinksInWareHouse.setTotalPrice(drinksInWareHouse.getTotalPrice() - purchasedDrinks.getTotalPrice());
        drinksInWareHouse.setCount(drinksInWareHouse.getCount() - purchasedDrinks.getCount());
        drinksInWareHouseRepository.save(drinksInWareHouse);
        checkingWarehouseIfValid(drinksInWareHouse);
    }

    private void setPurchasedDrinks(PurchasedDrinksRequest request, PurchasedDrinks purchasedDrinks) {
        purchasedDrinks.setBranch(branchRepository.findByIdAndDeleteFalse(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        purchasedDrinks.setEmployee(userRepository.findByIdAndBlockedFalse(request.getEmployeeId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        purchasedDrinks.setWarehouse(wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId()).orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND)));
    }

    private void checkingWarehouseIfValid(DrinksInWareHouse drinksInWareHouse) {
        if (drinksInWareHouse.getCount() < 0 || drinksInWareHouse.getTotalPrice() < 0) {
            throw new RecordNotFoundException(Constants.DRINKS_IN_WAREHOUSE_NOT_ENOUGH);
        }
    }

    private PurchasedDrinks findById(Integer integer) {
        return purchasedDrinksRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.PURCHASED_DRINKS_NOT_FOUND));
    }
}
