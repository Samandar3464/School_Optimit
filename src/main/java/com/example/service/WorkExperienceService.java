package com.example.service;

import com.example.entity.WorkExperience;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.WorkExperienceDto;
import com.example.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkExperienceService implements BaseService<WorkExperienceDto,Integer>{

    private final WorkExperienceRepository workExperienceRepository;

    @Override
    public ApiResponse create(WorkExperienceDto workExperienceDto) {
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceDto);
        checkIfExist(workExperienceDto);
        WorkExperience save = workExperienceRepository.save(workExperience);
        return new ApiResponse(save,true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(checkById(id),true);
    }

    @Override
    public ApiResponse update(WorkExperienceDto workExperienceDto) {
        WorkExperience workExperience = checkById(workExperienceDto.getId());
        WorkExperience experience = WorkExperience.toWorkExperience(workExperienceDto);
        experience.setId(workExperience.getId());
        workExperience = workExperienceRepository.save(experience);
        return new ApiResponse(workExperience,true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        WorkExperience workExperience = checkById(id);
        workExperienceRepository.delete(workExperience);
        return new ApiResponse(workExperience,true);
    }

    public WorkExperience checkById(Integer id) {
        return workExperienceRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.WORK_EXPERIENCE_NOT_FOUND));
    }

    public List<WorkExperience> checkAllById(List<Integer> workExperiences) {
        return workExperienceRepository.findAllById(workExperiences);
    }

    private void checkIfExist(WorkExperienceDto workExperienceDto) {
        boolean present = workExperienceRepository.findByPlaceOfWork(workExperienceDto.getPlaceOfWork()).isPresent();
        boolean position = workExperienceRepository.findByPosition(workExperienceDto.getPosition()).isPresent();
        if (present && position) {
            throw new RecordAlreadyExistException(Constants.WORK_EXPERIENCE_ALREADY_EXIST);
        }
    }
}
