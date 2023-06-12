package com.example.service;

import com.example.entity.TypeOfWork;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TypeOfWorkRequest;
import com.example.repository.TypeOfWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeOfWorkService implements BaseService<TypeOfWorkRequest, Integer> {

    private final TypeOfWorkRepository typeOfWorkRepository;

    @Override
    public ApiResponse create(TypeOfWorkRequest typeOfWorkRequest) {
        TypeOfWork typeOfWork = TypeOfWork.toTypeOfWork(typeOfWorkRequest);
        TypeOfWork save = typeOfWorkRepository.save(typeOfWork);
        return new ApiResponse(save,true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        TypeOfWork typeOfWork = checkById(id);
        return new ApiResponse(typeOfWork,true);
    }

    public ApiResponse getAll(){
        List<TypeOfWork> all = typeOfWorkRepository.findAll();
        return new ApiResponse(all,true);
    }

    @Override
    public ApiResponse update(TypeOfWorkRequest typeOfWorkRequest) {
        TypeOfWork typeOfWork = checkById(typeOfWorkRequest.getId());
        TypeOfWork set = TypeOfWork.toTypeOfWork(typeOfWorkRequest);
        set.setId(typeOfWork.getId());
        TypeOfWork save = typeOfWorkRepository.save(set);
        return new ApiResponse(save,true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        TypeOfWork typeOfWork = checkById(id);
        typeOfWorkRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED,true,typeOfWork);
    }

    public TypeOfWork checkById(Integer typeOfWorkRequest) {
        return typeOfWorkRepository.findById(typeOfWorkRequest).orElseThrow(() -> new RecordNotFoundException(Constants.TYPE_OF_WORK_NOT_FOUND));
    }
}
