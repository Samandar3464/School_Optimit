package com.example.service;

import com.example.entity.Branch;
import com.example.entity.MainBalance;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.MainBalanceRequest;
import com.example.model.response.MainBalanceResponse;
import com.example.repository.BranchRepository;
import com.example.repository.MainBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainBalanceService implements BaseService<MainBalanceRequest, Integer> {

    private final MainBalanceRepository mainBalanceRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(MainBalanceRequest mainBalanceRequest) {
        MainBalance mainBalance = MainBalance.toEntity(mainBalanceRequest);
        mainBalance.setBranch(getBranch(mainBalanceRequest.getBranchId()));
        mainBalanceRepository.save(mainBalance);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        MainBalance mainBalance = mainBalanceRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND));
        return new ApiResponse(Constants.SUCCESSFULLY, true, MainBalanceResponse.toResponse(mainBalance));
    }

    public ApiResponse getByBranchId(Integer branchId) {
        List<MainBalance> all = mainBalanceRepository.findAllByBranch_IdAndActiveTrue(branchId, Sort.by(Sort.Direction.DESC,"id"));
        List<MainBalanceResponse> allResponse = MainBalanceResponse.toAllResponse(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    @Override
    public ApiResponse update(MainBalanceRequest mainBalanceRequest) {
        getMainBalance(mainBalanceRequest.getId());
        MainBalance mainBalance = MainBalance.toEntity(mainBalanceRequest);
        mainBalance.setBranch(getBranch(mainBalanceRequest.getBranchId()));
        mainBalance.setId(mainBalanceRequest.getId());
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

    private Branch getBranch(Integer branchId) {
        return branchRepository.findByIdAndDeleteFalse(branchId).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
    }
}
