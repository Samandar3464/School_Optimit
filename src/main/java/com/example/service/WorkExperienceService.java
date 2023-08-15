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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkExperienceService implements BaseService<WorkExperienceRequest, Integer> {

    private final WorkExperienceRepository workExperienceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse create(WorkExperienceRequest workExperienceRequest) {
        checkIfExist(workExperienceRequest);
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceRequest);
        workExperience.setEmployee(userRepository.findById(workExperienceRequest.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        workExperienceRepository.save(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY, true, getWorkExperienceResponse(workExperience));
    }

    @Override
    public ApiResponse getById(Integer id) {
        WorkExperience workExperience = checkById(id);
        WorkExperienceResponse response = getWorkExperienceResponse(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private WorkExperienceResponse getWorkExperienceResponse(WorkExperience workExperience) {
        WorkExperienceResponse response = modelMapper.map(workExperience, WorkExperienceResponse.class);
        response.setStartDate(workExperience.getStartDate().toString());
        response.setEndDate(workExperience.getEndDate().toString());
        return response;
    }

    @Override
    public ApiResponse update(WorkExperienceRequest workExperienceRequest) {
        checkById(workExperienceRequest.getId());
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceRequest);
        workExperience.setEmployee(userRepository.findById(workExperienceRequest.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        workExperience.setId(workExperienceRequest.getId());
        workExperienceRepository.save(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY, true, getWorkExperienceResponse(workExperience));
    }

    @Override
    public ApiResponse delete(Integer id) {
        WorkExperience workExperience = checkById(id);
        workExperienceRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED, true, getWorkExperienceResponse(workExperience));
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllById(List<Integer> workExperiences) {
        List<WorkExperience> all = workExperienceRepository.findAllById(workExperiences);
        List<WorkExperienceResponse> allResponse = getWorkExperienceResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByUserId(Integer id) {
        List<WorkExperience> all = workExperienceRepository
                .findAllByEmployeeId(id, Sort.by(Sort.Direction.DESC, "id"));
        List<WorkExperienceResponse> allResponse = getWorkExperienceResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    private List<WorkExperienceResponse> getWorkExperienceResponses(List<WorkExperience> all) {
        List<WorkExperienceResponse> allResponse = new ArrayList<>();
        all.forEach(workExperience -> {
            allResponse.add(getWorkExperienceResponse(workExperience));
        });
        return allResponse;
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
