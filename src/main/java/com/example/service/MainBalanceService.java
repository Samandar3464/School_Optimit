package com.example.service;

import com.example.entity.MainBalance;
import com.example.entity.PaymentType;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.MainBalanceRequest;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.MainBalanceResponse;
import com.example.repository.BranchRepository;
import com.example.repository.MainBalanceRepository;
import com.example.repository.PaymentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainBalanceService implements BaseService<MainBalanceRequest, Integer> {

    private final MainBalanceRepository mainBalanceRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(MainBalanceRequest mainBalanceRequest) {
        MainBalance mainBalance = MainBalance.toEntity(mainBalanceRequest);
        mainBalance.setBranch(branchRepository.findById(mainBalanceRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        mainBalanceRepository.save(mainBalance);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        MainBalance mainBalance = getMainBalance(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, MainBalanceResponse.toResponse(mainBalance));
    }

    @Override
    public ApiResponse update(MainBalanceRequest mainBalanceRequest) {
        getMainBalance(mainBalanceRequest.getId());
        MainBalance mainBalance = MainBalance.toEntity(mainBalanceRequest);
        mainBalance.setId(mainBalanceRequest.getId());
        mainBalance.setBranch(branchRepository.findById(mainBalanceRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        mainBalanceRepository.save(mainBalance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, MainBalanceResponse.toResponse(mainBalance));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        MainBalance mainBalance = getMainBalance(integer);
        mainBalance.setActive(false);
        mainBalanceRepository.save(mainBalance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, MainBalanceResponse.toResponse(mainBalance));
    }

    private MainBalance getMainBalance(Integer integer) {
        return mainBalanceRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND));
    }
}
