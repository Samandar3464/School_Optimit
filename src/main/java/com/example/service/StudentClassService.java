package com.example.service;

import com.example.entity.Branch;
import com.example.entity.StudentClass;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.repository.BranchRepository;
import com.example.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentClassService implements BaseService<StudentClass, Integer> {

    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(StudentClass studentClass) {
        Branch branch = branchRepository.findById(studentClass.getComingBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        StudentClass from = StudentClass.from(studentClass, branch);
        studentClassRepository.save(from);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        return new ApiResponse(studentClass, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(StudentClass studentClass) {
        studentClassRepository.findById(studentClass.getId()).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        StudentClass build = StudentClass.builder()
                .id(studentClass.getId())
                .className(studentClass.getClassName())
                .startDate(studentClass.getStartDate())
                .endDate(studentClass.getEndDate())
                .build();
        studentClassRepository.save(build);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        studentClass.setActive(false);
        studentClassRepository.save(studentClass);
        return new ApiResponse(DELETED, true);
    }
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllActiveClasses(Integer branchId) {
        List<StudentClass> allByActiveTrue = studentClassRepository.findAllByActiveTrueAndBranchId(branchId);
        return new ApiResponse(allByActiveTrue, true);
    }
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllNeActiveClassesByYear(LocalDate startDate, LocalDate endDate,int id) {
        List<StudentClass> allByStartDateAfterAndEndDateBeforeAndActiveFalse = studentClassRepository.findAllByBranchIdAndStartDateAfterAndEndDateBeforeAndActiveFalse(id,startDate, endDate);
        return new ApiResponse(allByStartDateAfterAndEndDateBeforeAndActiveFalse, true);
    }
}
