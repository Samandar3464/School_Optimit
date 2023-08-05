package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Business;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.request.BranchDto;
import com.example.model.common.ApiResponse;
import com.example.model.response.BranchResponseListForAdmin;
import com.example.repository.BranchRepository;
import com.example.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.example.enums.Constants.*;

@RequiredArgsConstructor
@Service
public class BranchService implements BaseService<BranchDto, Integer> {

    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(BranchDto branchDto) {
        if (branchRepository.findByBusinessIdAndAddress(branchDto.getBusinessId(), branchDto.getAddress()).isPresent()) {
            throw new RecordNotFoundException(BRANCH_NAME_ALREADY_EXIST);
        }
        Business business = businessRepository.findByIdAndActiveTrueAndDeleteFalse(branchDto.getBusinessId()).orElseThrow(() -> new RecordNotFoundException(BUSINESS_NOT_FOUND));
        Branch branch = Branch.from(branchDto, business);
        branchRepository.save(branch);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(integer).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        return new ApiResponse(branch, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(BranchDto branchDto) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(branchDto.getId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        branch.setBusiness(businessRepository.findById(branchDto.getBusinessId()).orElseThrow(()-> new RecordNotFoundException(BUSINESS_NOT_FOUND)));
        branch.setName(branchDto.getName());
        branch.setAddress(branchDto.getAddress());
        branchRepository.save(branch);
        return new ApiResponse(SUCCESSFULLY, true,branch);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(integer).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        branch.setDelete(true);
        branchRepository.save(branch);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByBusinessId(Integer integer) {
        List<Branch> allByBusinessId = branchRepository.findAllByBusinessIdAndDeleteFalse(integer, Sort.by(Sort.Direction.DESC,"id"));
        if (allByBusinessId.isEmpty()) {
            throw new RecordNotFoundException(BRANCH_NOT_FOUND);
        }
        return new ApiResponse(allByBusinessId, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Branch> all = branchRepository.findAllByDeleteFalse(pageable);
        return new ApiResponse(new BranchResponseListForAdmin(
                all.getContent(), all.getTotalElements(), all.getTotalPages(), all.getNumber()), true);
    }
}
