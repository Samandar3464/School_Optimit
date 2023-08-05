package com.example.service;

import com.example.entity.WorkExperience;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.WorkExperienceRequest;
import com.example.model.response.WorkExperienceResponse;
import com.example.repository.UserRepository;
import com.example.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkExperienceService implements BaseService<WorkExperienceRequest, Integer> {

    private final WorkExperienceRepository workExperienceRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse create(WorkExperienceRequest workExperienceRequest) {
        checkIfExist(workExperienceRequest);
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceRequest);
        workExperience.setEmployee(userRepository.findById(workExperienceRequest.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        workExperienceRepository.save(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY, true, WorkExperienceResponse.toResponse(workExperience));
    }

    @Override
    public ApiResponse getById(Integer id) {
        WorkExperience workExperience = checkById(id);
        return new ApiResponse(Constants.SUCCESSFULLY, true, WorkExperienceResponse.toResponse(workExperience));
    }

    @Override
    public ApiResponse update(WorkExperienceRequest workExperienceRequest) {
        checkById(workExperienceRequest.getId());
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceRequest);
        workExperience.setEmployee(userRepository.findById(workExperienceRequest.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        workExperience.setId(workExperienceRequest.getId());
        workExperienceRepository.save(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY, true, WorkExperienceResponse.toResponse(workExperience));
    }

    @Override
    public ApiResponse delete(Integer id) {
        WorkExperience workExperience = checkById(id);
        workExperienceRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED, true, WorkExperienceResponse.toResponse(workExperience));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllById(List<Integer> workExperiences) {
        return new ApiResponse(Constants.SUCCESSFULLY, true, WorkExperienceResponse.toAllResponse(workExperienceRepository.findAllById(workExperiences)));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByUserId(Integer id) {
        return new ApiResponse(Constants.SUCCESSFULLY, true, WorkExperienceResponse.toAllResponse(workExperienceRepository.findAllByEmployeeId(id, Sort.by(Sort.Direction.DESC,"id"))));
    }

    private void checkIfExist(WorkExperienceRequest workExperienceRequest) {
        boolean present = workExperienceRepository
                .findByPlaceOfWorkAndPositionAndEmployeeIdAndStartDateAndEndDate(
                        workExperienceRequest.getPlaceOfWork(),
                        workExperienceRequest.getPosition(),
                        workExperienceRequest.getEmployeeId(),
                        workExperienceRequest.getStartDate(),
                        workExperienceRequest.getEndDate()).isPresent();
        if (present) {
            throw new RecordAlreadyExistException(Constants.WORK_EXPERIENCE_ALREADY_EXIST);
        }
    }

    public WorkExperience checkById(Integer id) {
        return workExperienceRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.WORK_EXPERIENCE_NOT_FOUND));
    }
}
