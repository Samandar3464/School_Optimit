package com.example.kitchen.service;

import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.kitchen.entity.Warehouse;
import com.example.kitchen.model.Response.WarehouseResponse;
import com.example.kitchen.model.request.WareHouseRequest;
import com.example.kitchen.repository.WareHouseRepository;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WareHouseService implements BaseService<WareHouseRequest, Integer> {

    private final WareHouseRepository wareHouseRepository;
    private final BranchRepository branchRepository;


    @Override
    public ApiResponse create(WareHouseRequest wareHouseRequest) {
        if (wareHouseRepository.findByNameAndActiveTrueAndBranchId(wareHouseRequest.getName(), wareHouseRequest.getBranchId()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.WAREHOUSE_ALREADY_EXIST);
        }
        Warehouse warehouse = Warehouse.toEntity(wareHouseRequest);
        warehouse.setBranch(branchRepository.findByIdAndDeleteFalse(wareHouseRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND)));
        wareHouseRepository.save(warehouse);
        return new ApiResponse(Constants.SUCCESSFULLY, true, warehouse);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        Warehouse warehouse = getWarehouse(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, warehouse);
    }

    public ApiResponse getAllByBranchId(Integer branchId, int page, int size) {
        Page<Warehouse> all = wareHouseRepository.findAllByActiveTrueAndBranchId(branchId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return new ApiResponse(Constants.SUCCESSFULLY, true, new WarehouseResponse(all.getContent(), all.getTotalElements(), all.getTotalPages(), all.getNumber()));
    }

    public ApiResponse getAll(int page, int size) {
        Page<Warehouse> all = wareHouseRepository.findAllByActiveTrue(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        return new ApiResponse(Constants.SUCCESSFULLY, true, new WarehouseResponse(all.getContent(), all.getTotalElements(), all.getTotalPages(), all.getNumber()));
    }

    @Override
    public ApiResponse update(WareHouseRequest wareHouseRequest) {
        getWarehouse(wareHouseRequest.getId());
        Warehouse warehouse = Warehouse.toEntity(wareHouseRequest);
        warehouse.setBranch(branchRepository.findByIdAndDeleteFalse(wareHouseRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND)));
        wareHouseRepository.save(warehouse);
        return new ApiResponse(Constants.SUCCESSFULLY, true, warehouse);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Warehouse warehouse = getWarehouse(integer);
        wareHouseRepository.delete(warehouse);
        return new ApiResponse(Constants.DELETED, true, warehouse);
    }

    private Warehouse getWarehouse(Integer id) {
        return wareHouseRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new RecordNotFoundException(Constants.WAREHOUSE_NOT_FOUND));
    }
}
