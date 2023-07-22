package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Level;
import com.example.entity.Subject;
import com.example.entity.SubjectLevel;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectLevelDto;
import com.example.repository.BranchRepository;
import com.example.repository.LevelRepository;
import com.example.repository.SubjectLevelRepository;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import static com.example.enums.Constants.*;

@RequiredArgsConstructor
@Service
public class SubjectLevelService implements BaseService<SubjectLevelDto, Integer> {

    private final SubjectRepository subjectRepository;
    private final LevelRepository levelRepository;
    private final SubjectLevelRepository subjectLevelRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(SubjectLevelDto dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        Level level = levelRepository.findById(dto.getLevelId()).orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        SubjectLevel subjectLevel = SubjectLevel.builder()
                .subject(subject)
                .level(level)
                .teachingHour(dto.getTeachingHour())
                .priceForPerHour(dto.getPriceForPerHour())
                .branch(branch)
                .build();
        subjectLevelRepository.save(subjectLevel);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        return new ApiResponse(subjectLevel, true);
    }

    @Override
    public ApiResponse update(SubjectLevelDto dto) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        Level level = levelRepository.findById(dto.getLevelId()).orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
        subjectLevel.setLevel(level);
        subjectLevel.setSubject(subject);
        subjectLevel.setPriceForPerHour(dto.getPriceForPerHour());
        subjectLevelRepository.save(subjectLevel);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        subjectLevelRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        subjectLevelRepository.deleteById(integer);
        return new ApiResponse(DELETED, true);
    }

    public ApiResponse getAllByBranchId(Integer branchId) {
        return new ApiResponse(subjectLevelRepository.findAllByBranchId(branchId), true);
    }

    public SubjectLevel getBySubjectIdAndLevelId(Integer subjectId, Integer levelId) {
        return subjectLevelRepository.findBySubjectIdAndLevelId(subjectId, levelId).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
    }
}
