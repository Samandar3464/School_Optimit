package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.enums.Constants;
import com.example.enums.MeasurementType;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.DailyConsumedProducts;
import com.example.kitchen.entity.ProductsInWareHouse;
import com.example.kitchen.entity.PurchasedProducts;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.ProductsInWareHouseResponse;
import com.example.kitchen.model.request.DailyConsumedProductsRequest;
import com.example.kitchen.model.request.ProductsInWareHouseRequest;
import com.example.kitchen.model.request.PurchasedProductsRequest;
import com.example.kitchen.repository.ProductsInWareHouseRepository;
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
public class ProductsInWareHouseService {

    private final ProductsInWareHouseRepository productsInWareHouseRepository;
    private final WareHouseRepository wareHouseRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;


    public ProductsInWareHouse create(ProductsInWareHouseRequest request) {
        ProductsInWareHouse productsInWareHouse = modelMapper.map(request, ProductsInWareHouse.class);
        setProductInWarehouse(request, productsInWareHouse);
        return productsInWareHouseRepository.save(productsInWareHouse);
    }

    public void storageOfPurchasedProducts(PurchasedProductsRequest request) {
        Optional<ProductsInWareHouse> productsInWareHouseOptional =
                productsInWareHouseRepository.findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(
                        request.getName(),
                        request.getMeasurementType(),
                        request.getBranchId(),
                        request.getWarehouseId());

        if (productsInWareHouseOptional.isPresent()) {
            ProductsInWareHouse productsInWareHouse = productsInWareHouseOptional.get();
            productsInWareHouse.setTotalPrice(productsInWareHouse.getTotalPrice() + request.getTotalPrice());
            productsInWareHouse.setQuantity(productsInWareHouse.getQuantity() + request.getQuantity());
            productsInWareHouseRepository.save(productsInWareHouse);
        } else {
            saveProductsToWarehouse(request);
        }
    }

    private void saveProductsToWarehouse(PurchasedProductsRequest request) {
        try {
            ProductsInWareHouseRequest productsInWareHouseRequest = modelMapper.map(request, ProductsInWareHouseRequest.class);
            create(productsInWareHouseRequest);
        } catch (Exception e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }

    public ProductsInWareHouse rollBackPurchasedProducts(PurchasedProducts old) {
        ProductsInWareHouse productsInWareHouse = productsInWareHouseRepository
                .findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(
                        old.getName(),
                        old.getMeasurementType(),
                        old.getBranch().getId(),
                        old.getWarehouse().getId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.PRODUCTS_IN_WAREHOUSE_NOT_FOUND));

        productsInWareHouse.setTotalPrice(productsInWareHouse.getTotalPrice() - old.getTotalPrice());
        productsInWareHouse.setQuantity(productsInWareHouse.getQuantity() - old.getQuantity());
        return productsInWareHouseRepository.save(productsInWareHouse);
    }

    public ApiResponse findByIdAndActiveTrue(Integer productInWarehouseId) {
        ProductsInWareHouse productsInWareHouse =
                productsInWareHouseRepository.findByIdAndActiveTrue(productInWarehouseId)
                .orElseThrow(() -> new RecordNotFoundException(Constants.PRODUCTS_IN_WAREHOUSE_NOT_FOUND));
        ProductsInWareHouseResponse wareHouseResponse =
                modelMapper.map(productsInWareHouse, ProductsInWareHouseResponse.class);
        return new ApiResponse(Constants.SUCCESSFULLY, true, wareHouseResponse);
    }

    public ApiResponse findAllByWarehouseIdAndActiveTrue(int page, int size, Integer wareHouseID) {
        Page<ProductsInWareHouse> all = productsInWareHouseRepository
                .findAllByWarehouseIdAndActiveTrue(wareHouseID, PageRequest.of(page, size));
        List<ProductsInWareHouseResponse> wareHouseResponses = new ArrayList<>();
        all.map(productsInWareHouse ->
                wareHouseResponses.add(modelMapper.map(productsInWareHouse, ProductsInWareHouseResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, wareHouseResponses);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        Page<ProductsInWareHouse> all = productsInWareHouseRepository
                .findAllByBranchIdAndActiveTrue(branchId, PageRequest.of(page, size));
        List<ProductsInWareHouseResponse> wareHouseResponses = new ArrayList<>();
        all.map(productsInWareHouse ->
                wareHouseResponses.add(modelMapper.map(productsInWareHouse, ProductsInWareHouseResponse.class)));
        return new ApiResponse(Constants.SUCCESSFULLY, true, wareHouseResponses);
    }

    private void setProductInWarehouse(ProductsInWareHouseRequest request, ProductsInWareHouse productsInWareHouse) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        Warehouse warehouse = wareHouseRepository.findByIdAndActiveTrue(request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));

        productsInWareHouse.setActive(true);
        productsInWareHouse.setBranch(branch);
        productsInWareHouse.setWarehouse(warehouse);
    }

    public void consumedProducts(DailyConsumedProductsRequest request) {
        ProductsInWareHouse productsInWareHouse = productsInWareHouseRepository
                .findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(
                        request.getName(),
                        request.getMeasurementType(),
                        request.getBranchId(),
                        request.getWarehouseId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.CONSUMED_PRODUCTS_NOT_FOUND));

        double unitPrice = productsInWareHouse.getTotalPrice() / productsInWareHouse.getQuantity();
        productsInWareHouse.setQuantity(productsInWareHouse.getQuantity() - request.getQuantity());
        productsInWareHouse.setTotalPrice(productsInWareHouse.getTotalPrice() - (request.getQuantity() * unitPrice));
        checkingForValid(productsInWareHouse);
        if (productsInWareHouse.getQuantity() == 0) {
            productsInWareHouse.setActive(false);
        }
        productsInWareHouseRepository.save(productsInWareHouse);
    }

    public void rollBackConsumedProducts(DailyConsumedProducts consumedProducts) {
        ProductsInWareHouse productsInWareHouse = productsInWareHouseRepository
                .findByNameAndMeasurementTypeAndBranchIdAndWarehouseIdAndActiveTrue(
                        consumedProducts.getName(),
                        consumedProducts.getMeasurementType(),
                        consumedProducts.getBranch().getId(),
                        consumedProducts.getWarehouse().getId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.CONSUMED_PRODUCTS_NOT_FOUND));

        double unitPrice = productsInWareHouse.getTotalPrice() / productsInWareHouse.getQuantity();
        productsInWareHouse.setQuantity(productsInWareHouse.getQuantity() + consumedProducts.getQuantity());
        productsInWareHouse.setTotalPrice(productsInWareHouse.getTotalPrice() + (consumedProducts.getQuantity() * unitPrice));
        checkingForValid(productsInWareHouse);
        productsInWareHouseRepository.save(productsInWareHouse);
    }

    public void checkingForValid(ProductsInWareHouse productsInWareHouse) {
        if (productsInWareHouse.getQuantity() < 0 || productsInWareHouse.getTotalPrice() < 0) {
            throw new RecordNotFoundException(Constants.PRODUCT_NOT_ENOUGH_QUANTITY);
        }
    }
}
