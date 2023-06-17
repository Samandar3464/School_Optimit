package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentAccountDto;
import com.example.model.response.StudentAccountResponse;
import com.example.repository.BranchRepository;
import com.example.repository.StudentAccountRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentAccountService implements BaseService<StudentAccountDto, Integer> {

    private final StudentAccountRepository repository;

    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(StudentAccountDto dto) {
        Optional<StudentAccount> byStudentId = repository.findByStudentId(dto.getStudentId());
        if (byStudentId.isPresent()){
            throw  new RecordAlreadyExistException(ACCOUNT_ALREADY_EXIST);
        }
        Student student = studentRepository.findById(dto.getStudentId()).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        StudentAccount studentAccount = StudentAccount.builder()
                .balance(dto.getBalance())
                .createdDate(LocalDateTime.now())
                .active(true)
                .branch(branch)
                .student(student)
                .build();
        repository.save(studentAccount);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        StudentAccount studentAccount = repository.findById(integer).orElseThrow(() -> new RecordNotFoundException(ACCOUNT_NOT_FOUND));
        return new ApiResponse(StudentAccountResponse.from(studentAccount), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(StudentAccountDto dto) {
        StudentAccount studentAccount = repository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(ACCOUNT_NOT_FOUND));
        studentAccount.setBalance(studentAccount.getBalance() + dto.getBalance());
        studentAccount.setUpdatedDate(LocalDateTime.now());
        repository.save(studentAccount);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        StudentAccount studentAccount = repository.findById(integer).orElseThrow(() -> new RecordNotFoundException(ACCOUNT_NOT_FOUND));
        studentAccount.setActive(false);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByBranchId(Integer integer) {
        List<StudentAccount> reasonList = repository.findAllByBranchIdAndActiveTrueOrderByCreatedDateAsc(integer);
        List<StudentAccountResponse> reasonResponseList = new ArrayList<>();
        reasonList.forEach(account -> reasonResponseList.add(StudentAccountResponse.from(account)));
        return new ApiResponse(reasonResponseList, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByStudentId(Integer integer) {
        StudentAccount account = repository.findByStudentIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(ACCOUNT_NOT_FOUND));;
        return new ApiResponse(StudentAccountResponse.from(account), true);
    }


}
