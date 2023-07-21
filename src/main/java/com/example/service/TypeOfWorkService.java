package com.example.service;

import com.example.entity.Branch;
import com.example.entity.TypeOfWork;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TypeOfWorkRequest;
import com.example.repository.BranchRepository;
import com.example.repository.TypeOfWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TypeOfWorkService implements BaseService<TypeOfWorkRequest, Integer> {

    private final TypeOfWorkRepository typeOfWorkRepository;
    private final BranchRepository branchRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(TypeOfWorkRequest typeOfWorkRequest) {
        Branch branch = branchRepository.findById(typeOfWorkRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        TypeOfWork typeOfWork = TypeOfWork.toTypeOfWork(typeOfWorkRequest, branch);
        typeOfWorkRepository.save(typeOfWork);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer id) {
        return new ApiResponse(checkById(id), true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByBranchId(Integer branchId) {
        return new ApiResponse(typeOfWorkRepository.findAllByBranchIdAndActiveTrue(branchId), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(TypeOfWorkRequest typeOfWorkRequest) {
        TypeOfWork typeOfWork = checkById(typeOfWorkRequest.getId());
        typeOfWork.setName(typeOfWorkRequest.getName());
        typeOfWork.setPriceForPerHour(typeOfWorkRequest.getPriceForPerHour());
        typeOfWorkRepository.save(typeOfWork);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer id) {
        TypeOfWork typeOfWork = checkById(id);
       typeOfWork.setActive(false);
        return new ApiResponse(DELETED, true);
    }

    public TypeOfWork checkById(Integer typeOfWorkRequest) {
        return typeOfWorkRepository.findById(typeOfWorkRequest).orElseThrow(() -> new RecordNotFoundException(Constants.TYPE_OF_WORK_NOT_FOUND));
    }
}
