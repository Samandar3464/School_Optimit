package com.example.service;

import com.example.entity.Family;
import com.example.entity.Student;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.response.FamilyResponse;
import com.example.repository.FamilyRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@RequiredArgsConstructor
@Service
public class FamilyService implements BaseService<Family, Integer> {

    private final FamilyRepository familyRepository;
    private final StudentRepository studentRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(Family family) {
        Family family1 = Family.from(family);
        Student student = studentRepository.findById(family.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        student.getFamily().add(family1);
        studentRepository.save(student);
        familyRepository.save(family1);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Family family = familyRepository.findById(integer)
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        return new ApiResponse(FamilyResponse.from(family), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(Family family) {
        familyRepository.findById(family.getId())
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        Family from = Family.from(family);
        from.setId(from.getId());
        familyRepository.save(from);
        return new ApiResponse(SUCCESSFULLY,true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Family family = familyRepository.findById(integer)
                .orElseThrow(() -> new UserNotFoundException(FAMILY_NOT_FOUND));
        family.setActive(false);
        familyRepository.save(family);
        return new ApiResponse(DELETED,true);
    }


    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getList(){
       List<Family> familyList= familyRepository.findAllByActiveTrue();
       List<FamilyResponse> familyResponses=new ArrayList<>();
       familyList.forEach(obj->{
           familyResponses.add(FamilyResponse.from(obj));});
       return new ApiResponse(familyResponses,true);
    }

}
