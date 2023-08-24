package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Family;
import com.example.entity.Student;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.FamilyLoginDto;
import com.example.model.request.FamilyRequest;
import com.example.model.response.FamilyResponse;
import com.example.model.response.FamilyResponseList;
import com.example.model.response.StudentResponse;
import com.example.repository.BranchRepository;
import com.example.repository.FamilyRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.enums.Constants.*;

@RequiredArgsConstructor
@Service
public class FamilyService implements BaseService<FamilyRequest, Integer> {

    private final FamilyRepository familyRepository;
    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse create(FamilyRequest familyRequest) {
        Family family = modelMapper.map(familyRequest, Family.class);
        setFamily(familyRequest, family);
        familyRepository.save(family);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    private void setFamily(FamilyRequest familyRequest, Family family) {
        Branch branch = branchRepository.findByIdAndDeleteFalse(familyRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Student student = studentRepository.findByIdAndActiveTrue(familyRequest.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));

        student.getFamilies().add(family);
        family.setBranch(branch);
        family.setRegisteredDate(LocalDateTime.now());
        family.setActive(true);
        studentRepository.save(student);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        Family family = familyRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        FamilyResponse response = modelMapper.map(family, FamilyResponse.class);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse update(FamilyRequest familyRequest) {
        Family old = familyRepository.findByIdAndActiveTrue(familyRequest.getId())
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        Family family = modelMapper.map(familyRequest, Family.class);
        setFamily(familyRequest, family);
        family.setId(familyRequest.getId());
        family.setFireBaseToken(old.getFireBaseToken());
        familyRepository.save(family);
        FamilyResponse response = modelMapper.map(family, FamilyResponse.class);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Family family = familyRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        Student student = studentRepository.findByIdAndActiveTrue(family.getStudent().getId())
                .orElseThrow(() -> new RecordNotFoundException(STUDENT_NOT_FOUND));

        student.getFamilies().remove(family);
        familyRepository.delete(family);
        studentRepository.save(student);
        FamilyResponse response = modelMapper.map(family, FamilyResponse.class);
        return new ApiResponse(DELETED, true, response);
    }


    public ApiResponse getList(int page, int size, int branchId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Family> familyList = familyRepository.findAllByBranchIdAndActiveTrue(branchId, pageable);
        List<FamilyResponse> familyResponses = new ArrayList<>();
        familyList.getContent().forEach(obj -> familyResponses.add(modelMapper.map(obj,FamilyResponse.class)));
        return new ApiResponse(new FamilyResponseList(familyResponses, familyList.getTotalElements(), familyList.getTotalPages(), familyList.getNumber()), true);
    }

    public ApiResponse familyLogIn(FamilyLoginDto dto) {
        Family family = familyRepository.findByPhoneNumberAndPassword(dto.getPhoneNumber(), dto.getPassword())
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        List<Student> allByFamily = studentRepository.findByFamiliesIn(List.of(Collections.singletonList(family)), Sort.by(Sort.Direction.DESC, "id"));
        List<StudentResponse> studentResponseDtoList = new ArrayList<>();
        allByFamily.forEach(student ->
                studentResponseDtoList.add(modelMapper.map(student, StudentResponse.class)));
        return new ApiResponse(studentResponseDtoList, true);
    }
}
