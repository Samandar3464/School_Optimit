package com.example.service;

import com.example.entity.Tariff;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TariffDto;
import com.example.repository.PermissionRepository;
import com.example.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TariffService implements BaseService<TariffDto, Integer> {

    private final TariffRepository tariffRepository;

    private final PermissionRepository permissionRepository;

    @Override
    public ApiResponse create(TariffDto tariffDto) {
        Tariff tariff = Tariff.toEntity(tariffDto, permissionRepository.findAllById(tariffDto.getPermissionsList()));
        tariffRepository.save(tariff);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(SUCCESSFULLY, true, tariffRepository.findByIdAndDeleteFalse(id).orElseThrow(() -> new RecordNotFoundException(TARIFF_NOT_FOUND)));
    }

    @Override
    public ApiResponse update(TariffDto tariffDto) {
        Tariff tariff = tariffRepository.findByIdAndDeleteFalse(tariffDto.getId()).orElseThrow(() -> new RecordNotFoundException(TARIFF_NOT_FOUND));
        Tariff entity = Tariff.toEntity(tariffDto, permissionRepository.findAllById(tariffDto.getPermissionsList()));
        entity.setId(tariffDto.getId());
        tariffRepository.save(entity);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Tariff tariff = tariffRepository.findByIdAndDeleteFalse(id).orElseThrow(() -> new RecordNotFoundException(TARIFF_NOT_FOUND));
        tariff.setDelete(true);
        tariffRepository.save(tariff);
        return new ApiResponse(DELETED, true);
    }

    public ApiResponse deActivate(Integer id) {
        Tariff tariff = tariffRepository.findByIdAndDeleteFalse(id).orElseThrow(() -> new RecordNotFoundException(TARIFF_NOT_FOUND));
        tariff.setActive(false);
        tariffRepository.save(tariff);
        return new ApiResponse(DEACTIVATED, true);
    }

    public ApiResponse activate(Integer id) {
        Tariff tariff = tariffRepository.findByIdAndDeleteFalse(id).orElseThrow(() -> new RecordNotFoundException(TARIFF_NOT_FOUND));
        tariff.setActive(true);
        tariffRepository.save(tariff);
        return new ApiResponse(ACTIVATED, true);
    }

    public ApiResponse getTariffListForAdmin() {
        return new ApiResponse(SUCCESSFULLY, true, tariffRepository.findAllByDeleteFalseOrderByPrice());
    }

    public ApiResponse getTariffListForUser() {
        return new ApiResponse(SUCCESSFULLY, true, tariffRepository.findAllByActiveTrueAndDeleteFalseOrderByPrice());
    }
}
