package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.ProductsInWareHouse;
import com.example.kitchen.entity.PurchasedProducts;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.PurchasedProductsResponse;
import com.example.kitchen.model.request.PurchasedProductsRequest;
import com.example.kitchen.repository.PurchasedProductsRepository;
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
public class PurchasedProductsService implements BaseService<PurchasedProductsRequest, Integer> {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final WareHouseRepository wareHouseRepository;
    private final PurchasedProductsRepository purchasedProductsRepository;
    private final ProductsInWareHouseService productsInWareHouseService;

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse create(PurchasedProductsRequest request) {
        productsInWareHouseService.storageOfPurchasedProducts(request);
        PurchasedProducts purchasedProducts = modelMapper.map(request, PurchasedProducts.class);
        setPurchasedProducts(request, purchasedProducts);
        purchasedProductsRepository.save(purchasedProducts);
        PurchasedProductsResponse response =
                modelMapper.map(purchasedProducts, PurchasedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        PurchasedProducts purchasedProducts = getByPurchasedProductId(integer);
        PurchasedProductsResponse response =
                modelMapper.map(purchasedProducts, PurchasedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse update(PurchasedProductsRequest request) {
        productInspectionAndStorage(request);
        PurchasedProducts purchasedProducts = modelMapper.map(request, PurchasedProducts.class);
        purchasedProducts.setId(request.getId());
        setPurchasedProducts(request, purchasedProducts);
        purchasedProductsRepository.save(purchasedProducts);
        PurchasedProductsResponse response =
                modelMapper.map(purchasedProducts, PurchasedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private void productInspectionAndStorage(PurchasedProductsRequest request) {
        PurchasedProducts oldPurchasedProducts = getByPurchasedProductId(request.getId());
        ProductsInWareHouse productsInWareHouse =
                productsInWareHouseService.rollBackPurchasedProducts(oldPurchasedProducts);
        productsInWareHouseService.storageOfPurchasedProducts(request);
        productsInWareHouseService.checkingForValid(productsInWareHouse);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse delete(Integer integer) {
        PurchasedProducts purchasedProducts = getByPurchasedProductId(integer);
        purchasedProducts.setActive(false);
        purchasedProductsRepository.save(purchasedProducts);

        ProductsInWareHouse productsInWareHouse =
                productsInWareHouseService.rollBackPurchasedProducts(purchasedProducts);
        productsInWareHouseService.checkingForValid(productsInWareHouse);

        PurchasedProductsResponse response =
                modelMapper.map(purchasedProducts, PurchasedProductsResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private void setPurchasedProducts(PurchasedProductsRequest request, PurchasedProducts purchasedProducts) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        User user = userRepository.findByIdAndBlockedFalse(request.getEmployeeId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
        Warehouse warehouse = wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));

        purchasedProducts.setActive(true);
        purchasedProducts.setBranch(branch);
        purchasedProducts.setEmployee(user);
        purchasedProducts.setWarehouse(warehouse);
    }

    private PurchasedProducts getByPurchasedProductId(Integer integer) {
        return purchasedProductsRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.PURCHASED_PRODUCTS_NOT_FOUND));
    }

    public ApiResponse getAllByWarehouseId(Integer warehouseId, int page, int size) {
        List<PurchasedProductsResponse> responses = new ArrayList<>();
        Page<PurchasedProducts> all = purchasedProductsRepository
                .findAllByWarehouseIdAndActiveTrue(warehouseId, PageRequest.of(page, size));
        all.map(purchasedProducts ->
                responses.add(modelMapper.map(purchasedProducts, PurchasedProductsResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        List<PurchasedProductsResponse> responses = new ArrayList<>();
        Page<PurchasedProducts> all = purchasedProductsRepository
                .findAllByBranch_IdAndActiveTrue(branchId, PageRequest.of(page, size));
        all.map(purchasedProducts ->
                responses.add(modelMapper.map(purchasedProducts, PurchasedProductsResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, responses);
    }
}
