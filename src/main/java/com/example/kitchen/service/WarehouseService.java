package com.example.kitchen.service;

import com.example.entity.Branch;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.Product;
import com.example.kitchen.entity.ProductAndQuantity;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.ProductAndQuantityDto;
import com.example.kitchen.model.WarehouseDto;
import com.example.kitchen.model.WarehouseResponse;
import com.example.kitchen.repository.ProductAndQuantityRepository;
import com.example.kitchen.repository.ProductRepository;
import com.example.kitchen.repository.WarehouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import com.example.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class WarehouseService implements BaseService<WarehouseDto, Integer> {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final BranchRepository branchRepository;
    private final ProductAndQuantityRepository productAndQuantityRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(WarehouseDto dto) {
        if (warehouseRepository.existsByBranchIdAndActiveTrue(dto.getBranchId())) {
            throw new RecordAlreadyExistException(WAREHOUSE_ALREADY_EXIST);
        }
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Warehouse warehouse = Warehouse.builder()
                .name(dto.getName())
                .active(true)
                .branch(branch)
                .build();
        warehouseRepository.save(warehouse);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Warehouse warehouse = warehouseRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(WAREHOUSE_NOT_FOUND));
        List<ProductAndQuantity> allByWarehouseId = productAndQuantityRepository.findAllByWarehouseId(warehouse.getId());
        return new ApiResponse(new WarehouseResponse(warehouse, allByWarehouseId), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(WarehouseDto dto) {
        Warehouse warehouse = warehouseRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(WAREHOUSE_NOT_FOUND));
        warehouse.setName(dto.getName());
        warehouseRepository.save(warehouse);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Warehouse warehouse = warehouseRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(WAREHOUSE_NOT_FOUND));
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse addProductToWarehouse(List<ProductAndQuantityDto> dto, Integer warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow(() -> new RecordNotFoundException(WAREHOUSE_NOT_FOUND));
        List<ProductAndQuantity> allByWarehouseId = productAndQuantityRepository.findAllByWarehouseId(warehouseId);
        if (allByWarehouseId.isEmpty()) {
            dto.forEach(obj -> {
                Product product = productRepository.findById(obj.getProductId()).orElseThrow(() -> new RecordNotFoundException(PRODUCT_NOT_FOUND));
                ProductAndQuantity productAndQuantity1 = ProductAndQuantity.builder().product(product).quantity(obj.getQuantity()).warehouse(warehouse).build();
                productAndQuantityRepository.save(productAndQuantity1);
            });
            return new ApiResponse(SUCCESSFULLY, true);
        }
        for (ProductAndQuantityDto obj : dto) {
            Optional<ProductAndQuantity> optional = productAndQuantityRepository.findByWarehouseIdAndProductId(warehouseId, obj.getProductId());
            if (optional.isPresent()) {
                optional.get().setQuantity(optional.get().getQuantity() + obj.getQuantity());
                productAndQuantityRepository.save(optional.get());
            } else {
                Product product = productRepository.findById(obj.getProductId()).orElseThrow(() -> new RecordNotFoundException(PRODUCT_NOT_FOUND));
                ProductAndQuantity productAndQuantity1 = ProductAndQuantity.builder().product(product).quantity(obj.getQuantity()).warehouse(warehouse).build();
                productAndQuantityRepository.save(productAndQuantity1);
            }
        }
        return new ApiResponse(SUCCESSFULLY, true);
    }


//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse getByBranchId(Integer page, Integer size, Integer branchId) {
//        Pageable page1 = PageRequest.of(page, size);
//        Page<Warehouse> warehouses = warehouseRepository.findAllByBranchIdAndActiveTrue(branchId, page1);
//        MeasurementResponseList productResponseList = MeasurementResponseList.builder()
//                .measurements(warehouses.getContent())
//                .totalElement(warehouses.getTotalElements())
//                .totalPage(warehouses.getTotalPages())
//                .size(warehouses.getSize())
//                .build();
//        return new ApiResponse(productResponseList, true);
//    }
}
