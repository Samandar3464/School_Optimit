package com.example.service;

import com.example.entity.User;
import com.example.entity.WorkExperience;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.WorkExperienceDto;
import com.example.repository.UserRepository;
import com.example.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class WorkExperienceService implements BaseService<WorkExperienceDto, Integer> {

    private final WorkExperienceRepository workExperienceRepository;
    private final UserRepository userRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(WorkExperienceDto dto) {
        if (workExperienceRepository.findByPlaceOfWorkAndPositionAndEmployeeIdAndStartDateAfterAndEndDateBefore(dto.getPlaceOfWork(),
                dto.getPosition(), dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate()).isPresent()) {
            throw new RecordAlreadyExistException(WORK_EXPERIENCE_ALREADY_EXIST);
        }
        User user = userRepository.findById(dto.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        WorkExperience workExperience = WorkExperience.toWorkExperience(dto, user);
        workExperienceRepository.save(workExperience);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer id) {
        return new ApiResponse(checkById(id), true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(WorkExperienceDto dto) {
        checkById(dto.getId());
        User user = userRepository.findById(dto.getEmployeeId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        WorkExperience experience = WorkExperience.toWorkExperience(dto, user);
        experience.setId(dto.getId());
        workExperienceRepository.save(experience);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer id) {
        checkById(id);
        workExperienceRepository.deleteById(id);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllByUserId(Integer id) {
        List<WorkExperienceDto> workExperienceDtoList = new ArrayList<>();
        workExperienceRepository.findAllByEmployeeId(id).forEach(workExperience -> {
            workExperienceDtoList.add(WorkExperienceDto.toWorkExperienceDto(workExperience));
        });
        return new ApiResponse(workExperienceDtoList, true);
    }

    private WorkExperienceDto checkById(Integer id) {
        return WorkExperienceDto.toWorkExperienceDto(workExperienceRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(WORK_EXPERIENCE_NOT_FOUND)));
    }

}
