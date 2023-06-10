package com.example.service;

import com.example.entity.Tariff;
import com.example.enums.Constants;
import com.example.enums.Lifetime;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TariffDto;
import com.example.model.response.TariffResponse;
import com.example.repository.PermissionRepository;
import com.example.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository repository;

    private final PermissionRepository permissionRepository;

    public ApiResponse save(TariffDto tariffDto) {
        Tariff tariff = Tariff.toEntity(tariffDto);
        tariff.setPermissions(permissionRepository.findAllById(tariffDto.getPermissionsList()));
        repository.save(tariff);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse getTariffList() {
        List<Tariff> tariffList = repository.findAllByDelete(false);
        tariffList.sort(Comparator.comparing(Tariff::getPrice));
        List<TariffResponse> tariffResponseList = toTariffResponse(tariffList);
        return new ApiResponse(Constants.FOUND, true, tariffResponseList);
    }


    public ApiResponse getToChooseATariff() {
        List<Tariff> tariffList = repository.findAllByActiveAndDelete(true,false);
        tariffList.sort(Comparator.comparing(Tariff::getPrice));
        List<TariffResponse> tariffResponse = toTariffResponse(tariffList);
        return new ApiResponse(Constants.FOUND, true, tariffResponse);
    }

    public ApiResponse getById(Integer id) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            throw new RecordNotFoundException(Constants.TARIFF_NOT_FOUND);
        }
        Tariff tariff = optionalTariff.get();
        TariffResponse response = TariffResponse.toResponse(tariff);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse update(Integer id, TariffDto tariffDto) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            throw new RecordNotFoundException(Constants.TARIFF_NOT_FOUND);
        }
        Tariff tariff = optionalTariff.get();
        setTariff(tariffDto, tariff);
        repository.save(tariff);

        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse remove(Integer id) {
        Optional<Tariff> optionalTariff = repository.findById(id);
        if (optionalTariff.isEmpty()) {
            throw new RecordNotFoundException(Constants.TARIFF_NOT_FOUND);
        }
        Tariff tariff = optionalTariff.get();
        tariff.setDelete(true);
        repository.save(tariff);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }


    private static List<TariffResponse> toTariffResponse(List<Tariff> tariffList) {
        List<TariffResponse> tariffResponseList = new ArrayList<>();
        tariffList.forEach(tariff -> {
            tariffResponseList.add(TariffResponse.toResponse(tariff));
        });
        return tariffResponseList;
    }

    private void setTariff(TariffDto tariffDto, Tariff tariff) {
        tariff.setTradeAmount(tariffDto.getTradeAmount());
        tariff.setProductAmount(tariffDto.getProductAmount());
        tariff.setDelete(tariffDto.isDelete());
        tariff.setName(tariffDto.getName());
        tariff.setActive(tariffDto.isActive());
        tariff.setDescription(tariffDto.getDescription());
        tariff.setDiscount(tariffDto.getDiscount());
        tariff.setInterval(tariffDto.getInterval());
        tariff.setPrice(tariffDto.getPrice());
        tariff.setBranchAmount(tariffDto.getBranchAmount());
        tariff.setEmployeeAmount(tariffDto.getEmployeeAmount());
       try {
           tariff.setLifetime(Lifetime.valueOf(tariffDto.getLifetime()));
       }catch (Exception e){
           throw new RecordNotFoundException(Constants.LIFE_TIME_DONT_MATCH + "    "+e);
       }
        tariff.setTestDay(tariffDto.getTestDay());
        tariff.setPermissions(permissionRepository.findAllById(tariffDto.getPermissionsList()));
    }
}
