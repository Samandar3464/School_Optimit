package com.example.service;

import com.example.entity.SubjectLevel;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectLevelDto;
import com.example.repository.BranchRepository;
import com.example.repository.LevelRepository;
import com.example.repository.SubjectLevelRepository;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
        SubjectLevel subjectLevel = new SubjectLevel();
        setSubjectLevel(dto, subjectLevel);
        subjectLevelRepository.save(subjectLevel);
        return new ApiResponse(SUCCESSFULLY, true, subjectLevel);
    }

    private void setSubjectLevel(SubjectLevelDto dto, SubjectLevel subjectLevel) {
        subjectLevel.setTeachingHour(dto.getTeachingHour());
        subjectLevel.setSubject(subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND)));
        subjectLevel.setLevel(levelRepository.findById(dto.getLevelId()).orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND)));
        subjectLevel.setBranch(branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        return new ApiResponse(subjectLevel, true);
    }

    @Override
    public ApiResponse update(SubjectLevelDto dto) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        setSubjectLevel(dto, subjectLevel);
        subjectLevelRepository.save(subjectLevel);
        return new ApiResponse(SUCCESSFULLY, true, subjectLevel);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
        subjectLevelRepository.deleteById(integer);
        return new ApiResponse(DELETED, true, subjectLevel);
    }

    public ApiResponse getAllByBranchId(Integer branchId) {
        return new ApiResponse(subjectLevelRepository.findAllByBranchId(branchId, Sort.by(Sort.Direction.DESC, "id")), true);
    }

    public SubjectLevel getBySubjectIdAndLevelId(Integer subjectId, Integer levelId) {
        return subjectLevelRepository.findBySubjectIdAndLevelId(subjectId, levelId).orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
    }
}
