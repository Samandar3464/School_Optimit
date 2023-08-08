package com.example.kitchen.service;

import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DrinksInWareHouse;
//import com.example.kitchen.model.response.DrinkResponse;
import com.example.kitchen.repository.DrinkRepository;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrinkService implements BaseService<DrinkRequest, Integer> {

    private final DrinkRepository drinkRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;

    @Override
    public ApiResponse create(DrinkRequest drinkRequest) {
//        Optional<DrinksInWareHouseResponse> drinkOptional = drinkRepository.findByNameAndActiveTrueAndLiterQuantityAndWarehouseId(drinkRequest.getName(), drinkRequest.getLiterQuantity(), drinkRequest.getUnitPrice(), drinkRequest.getWarehouseId());
//        if (drinkOptional.isPresent()) {
//            DrinksInWareHouseResponse drinksInWareHouse = drinkOptional.get();
//            drinksInWareHouse.setCount(drinksInWareHouse.getCount() + drinkRequest.getCount());
////            drinksInWareHouse.setTotalPrice(drinksInWareHouse.getTotalPrice() + drinksInWareHouse.getTotalPrice());
//        }
        DrinksInWareHouse drinksInWareHouse = DrinksInWareHouse.toEntity(drinkRequest);
        setDrink(drinkRequest, drinksInWareHouse);
        drinkRepository.save(drinksInWareHouse);
        return new ApiResponse(Constants.SUCCESSFULLY, true, drinksInWareHouse);
    }

    private void setDrink(DrinkRequest drinkRequest, DrinksInWareHouse drinksInWareHouse) {
        drinksInWareHouse.setBranch(branchRepository.findByIdAndDeleteFalse(drinkRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        drinksInWareHouse.setWarehouse(wareHouseRepository.findByIdAndActiveTrue(drinkRequest.getWarehouseId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return null;
    }

    @Override
    public ApiResponse update(DrinkRequest drinkRequest) {
        return null;
    }

    @Override
    public ApiResponse delete(Integer integer) {
        return null;
    }
}
