package com.example.service;

import com.example.entity.TypeOfWork;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TypeOfWorkRequest;
import com.example.repository.BranchRepository;
import com.example.repository.TypeOfWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeOfWorkService implements BaseService<TypeOfWorkRequest, Integer> {

    private final TypeOfWorkRepository typeOfWorkRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(TypeOfWorkRequest typeOfWorkRequest) {
        TypeOfWork typeOfWork = TypeOfWork.toTypeOfWork(typeOfWorkRequest);
        typeOfWork.setBranch(branchRepository.findById(typeOfWorkRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        typeOfWorkRepository.save(typeOfWork);
        return new ApiResponse(Constants.SUCCESSFULLY, true, typeOfWork);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(Constants.SUCCESSFULLY, true, checkById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByBranchId(Integer branchId) {
        List<TypeOfWork> all = typeOfWorkRepository.findAllByBranch_Id(branchId, Sort.by(Sort.Direction.DESC, "id"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    @Override
    public ApiResponse update(TypeOfWorkRequest typeOfWorkRequest) {
        checkById(typeOfWorkRequest.getId());
        TypeOfWork typeOfWork = TypeOfWork.toTypeOfWork(typeOfWorkRequest);
        typeOfWork.setBranch(branchRepository.findById(typeOfWorkRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        typeOfWork.setId(typeOfWorkRequest.getId());
        typeOfWorkRepository.save(typeOfWork);
        return new ApiResponse(Constants.SUCCESSFULLY, true, typeOfWork);
    }

    @Override
    public ApiResponse delete(Integer id) {
        TypeOfWork typeOfWork = checkById(id);
        typeOfWorkRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED, true, typeOfWork);
    }

    public TypeOfWork checkById(Integer typeOfWorkRequest) {
        return typeOfWorkRepository.findById(typeOfWorkRequest).orElseThrow(() -> new RecordNotFoundException(Constants.TYPE_OF_WORK_NOT_FOUND));
    }
}
