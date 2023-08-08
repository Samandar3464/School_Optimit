package com.example.kitchen.service;

import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.ProductsInWareHouse;
import com.example.kitchen.entity.PurchasedProducts;
import com.example.kitchen.model.Response.PurchasedProductsResponse;
import com.example.kitchen.model.request.PurchasedProductsRequest;
import com.example.kitchen.repository.ProductsInWareHouseRepository;
import com.example.kitchen.repository.PurchasedProductsRepository;
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
public class PurchasedProductsService implements BaseService<PurchasedProductsRequest, Integer> {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final PurchasedProductsRepository purchasedProductsRepository;
    private final ProductsInWareHouseRepository productsInWareHouseRepository;

    @Override
    public ApiResponse create(PurchasedProductsRequest request) {
        PurchasedProducts purchasedProducts = PurchasedProducts.toEntity(request);
        setPurchasedProducts(request, purchasedProducts);
        checkingPurchasedProductsAndSetInWarehouse(request);
        purchasedProductsRepository.save(purchasedProducts);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedProductsResponse.toResponse(purchasedProducts));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        PurchasedProducts purchasedProducts = findById(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedProductsResponse.toResponse(purchasedProducts));
    }

    @Override
    @ExceptionHandler(RecordNotFoundException.class)
    public ApiResponse update(PurchasedProductsRequest request) {
        rollBackPurchasedProducts(findById(request.getId()));
        checkingPurchasedProductsAndSetInWarehouse(request);

        PurchasedProducts purchasedProducts = PurchasedProducts.toEntity(request);
        purchasedProducts.setId(request.getId());
        setPurchasedProducts(request, purchasedProducts);

        purchasedProductsRepository.save(purchasedProducts);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedProductsResponse.toResponse(purchasedProducts));
    }

    @Override
    @ExceptionHandler(RecordNotFoundException.class)
    public ApiResponse delete(Integer integer) {
        PurchasedProducts purchasedProducts = findById(integer);
        purchasedProducts.setActive(false);
        rollBackPurchasedProducts(purchasedProducts);
        purchasedProductsRepository.save(purchasedProducts);
        return new ApiResponse(Constants.SUCCESSFULLY, true, PurchasedProductsResponse.toResponse(purchasedProducts));
    }


    private void rollBackPurchasedProducts(PurchasedProducts old) {
        ProductsInWareHouse productsInWareHouse = productsInWareHouseRepository.findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(old.getName(), old.getMeasurementType(), old.getBranch().getId(), old.getWarehouse().getId()).orElseThrow(() -> new RecordNotFoundException(Constants.PRODUCTS_IN_WAREHOUSE_NOT_FOUND));
        productsInWareHouse.setTotalPrice(productsInWareHouse.getTotalPrice() - old.getTotalPrice());
        productsInWareHouse.setQuantity(productsInWareHouse.getQuantity() - old.getQuantity());
        checkingForValid(productsInWareHouse);
        productsInWareHouseRepository.save(productsInWareHouse);
    }

    private void checkingPurchasedProductsAndSetInWarehouse(PurchasedProductsRequest request) {
        Optional<ProductsInWareHouse> productsInWareHouse = productsInWareHouseRepository.findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(request.getName(), request.getMeasurementType(), request.getBranchId(), request.getWarehouseId());
        if (productsInWareHouse.isPresent()) {
            productsInWareHouse.get().setTotalPrice(productsInWareHouse.get().getTotalPrice() + request.getTotalPrice());
            productsInWareHouse.get().setQuantity(productsInWareHouse.get().getQuantity() + request.getQuantity());
            productsInWareHouseRepository.save(productsInWareHouse.get());
        }
    }

    private static void checkingForValid(ProductsInWareHouse productsInWareHouse) {
        if (productsInWareHouse.getQuantity() < 0 || productsInWareHouse.getTotalPrice() < 0) {
            throw new RecordNotFoundException(Constants.PRODUCT_NOT_ENOUGH_QUANTITY);
        }
    }

    private void setPurchasedProducts(PurchasedProductsRequest request, PurchasedProducts purchasedProducts) {
        purchasedProducts.setBranch(branchRepository.findByIdAndDeleteFalse(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        purchasedProducts.setEmployee(userRepository.findByIdAndBlockedFalse(request.getEmployeeId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        purchasedProducts.setWarehouse(wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId()).orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND)));
    }

    private PurchasedProducts findById(Integer integer) {
        return purchasedProductsRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.PURCHASED_PRODUCTS_NOT_FOUND));
    }
}
