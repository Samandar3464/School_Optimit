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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkExperienceService implements BaseService<WorkExperienceDto,Integer>{

    private final WorkExperienceRepository workExperienceRepository;

    @Override
    public ApiResponse create(WorkExperienceDto workExperienceDto) {
        checkIfExist(workExperienceDto);
        WorkExperience workExperience = WorkExperience.toWorkExperience(workExperienceDto);
        workExperienceRepository.save(workExperience);
        return new ApiResponse(Constants.SUCCESSFULLY,true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(checkById(id),true);
    }

    @Override
    public ApiResponse update(WorkExperienceDto workExperienceDto) {
        checkById(workExperienceDto.getId());
        WorkExperience experience = WorkExperience.toWorkExperience(workExperienceDto);
        experience.setId(workExperienceDto.getId());
        workExperienceRepository.save(experience);
        return new ApiResponse(Constants.SUCCESSFULLY,true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        WorkExperienceDto workExperience = checkById(id);
        workExperienceRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED,true,workExperience);
    }

    public WorkExperienceDto checkById(Integer id) {
        return WorkExperienceDto.toWorkExperienceDto(workExperienceRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(Constants.WORK_EXPERIENCE_NOT_FOUND)));
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

    public List<WorkExperience> toAllEntity(List<WorkExperienceDto> workExperiences) {
        List<WorkExperience> workExperienceList = new ArrayList<>();
        for (WorkExperienceDto workExperience : workExperiences) {
            workExperienceList.add(WorkExperience.toWorkExperience(workExperience));
        }
        return workExperienceList;
    }

    public List<WorkExperience> saveAll(List<WorkExperience> allEntity) {
        return workExperienceRepository.saveAll(allEntity);
    }
}
