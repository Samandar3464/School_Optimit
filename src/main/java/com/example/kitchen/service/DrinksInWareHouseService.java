package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DrinksInWareHouse;
import com.example.kitchen.entity.PurchasedDrinks;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.DrinksInWareHouseResponse;
import com.example.kitchen.model.request.DrinksInWareHouseRequest;
import com.example.kitchen.model.request.PurchasedDrinksRequest;
import com.example.kitchen.repository.DrinksInWareHouseRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrinksInWareHouseService {

    private final ModelMapper modelMapper;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final DrinksInWareHouseRepository drinksInWareHouseRepository;

    public DrinksInWareHouse create(DrinksInWareHouseRequest request) {
        DrinksInWareHouse drinksInWareHouse = modelMapper.map(request, DrinksInWareHouse.class);
        setDrinksWarehouse(request, drinksInWareHouse);
        return drinksInWareHouseRepository.save(drinksInWareHouse);
    }


    public void storageOfPurchasedDrinks(PurchasedDrinksRequest request) {
        Optional<DrinksInWareHouse> drinksInWareHouseOptional = drinksInWareHouseRepository
                .findByNameAndLiterQuantityAndBranchIdAndWarehouseIdAndActiveTrue(
                        request.getName(),
                        request.getLiterQuantity(),
                        request.getBranchId(),
                        request.getWarehouseId());

        if (drinksInWareHouseOptional.isPresent()) {
            DrinksInWareHouse drinksInWareHouse = drinksInWareHouseOptional.get();
            drinksInWareHouse.setCount(drinksInWareHouse.getCount() + request.getCount());
            drinksInWareHouseRepository.save(drinksInWareHouse);
        }else {
            saveDrinks(request);
        }
    }

    public DrinksInWareHouse rollBackPurchasedDrinks(PurchasedDrinks purchasedDrinks) {
        DrinksInWareHouse drinksInWareHouse = drinksInWareHouseRepository
                .findByNameAndLiterQuantityAndBranchIdAndWarehouseIdAndActiveTrue(
                        purchasedDrinks.getName(),
                        purchasedDrinks.getLiterQuantity(),
                        purchasedDrinks.getBranch().getId(),
                        purchasedDrinks.getWarehouse().getId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.DRINKS_IN_WAREHOUSE_NOT_FOUND));

        drinksInWareHouse.setCount(drinksInWareHouse.getCount() - purchasedDrinks.getCount());
       return drinksInWareHouseRepository.save(drinksInWareHouse);
    }

    private void saveDrinks(PurchasedDrinksRequest request) {
        try {
            DrinksInWareHouseRequest drinksInWareHouseRequest = modelMapper.map(request, DrinksInWareHouseRequest.class);
            create(drinksInWareHouseRequest);
        }catch (Exception e){
            throw new RecordNotFoundException(e.getMessage());
        }
    }


    private void setDrinksWarehouse(DrinksInWareHouseRequest request, DrinksInWareHouse drinksInWareHouse) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        Warehouse warehouse = wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));

        drinksInWareHouse.setActive(true);
        drinksInWareHouse.setBranch(branch);
        drinksInWareHouse.setWarehouse(warehouse);
    }

    public ApiResponse getAllByWarehouseId(Integer warehouseId, int page, int size) {
        List<DrinksInWareHouseResponse> response = new ArrayList<>();
        Page<DrinksInWareHouse> all = drinksInWareHouseRepository
                .findAllByWarehouseIdAndActiveTrue(warehouseId, PageRequest.of(page, size));
        all.map(drinksInWareHouse ->
                response.add(modelMapper.map(drinksInWareHouse,DrinksInWareHouseResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        List<DrinksInWareHouseResponse> response = new ArrayList<>();
        Page<DrinksInWareHouse> all = drinksInWareHouseRepository
                .findAllByBranchIdAndActiveTrue(branchId, PageRequest.of(page, size));
        all.map(drinksInWareHouse ->
                response.add(modelMapper.map(drinksInWareHouse,DrinksInWareHouseResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse getById(Integer drinksInWarehouseId) {
        DrinksInWareHouse drinksInWareHouse = drinksInWareHouseRepository.findByIdAndActiveTrue(drinksInWarehouseId)
                .orElseThrow(() -> new RecordNotFoundException(Constants.DRINKS_IN_WAREHOUSE_NOT_FOUND));
        DrinksInWareHouseResponse response =
                modelMapper.map(drinksInWareHouse, DrinksInWareHouseResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }
}
