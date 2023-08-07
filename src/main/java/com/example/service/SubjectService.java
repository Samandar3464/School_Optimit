package com.example.service;

import com.example.entity.Subject;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequestDto;
import com.example.model.response.SubjectResponse;
import com.example.model.response.TopicResponse;
import com.example.repository.BranchRepository;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class SubjectService implements BaseService<SubjectRequestDto, Integer> {

    private final SubjectRepository subjectRepository;
    private final TopicService topicService;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(SubjectRequestDto dto) {
        if (subjectRepository.findByName(dto.getName()).isPresent()) {
            throw new RecordAlreadyExistException(SUBJECT_ALREADY_EXIST);
        }
        Subject subject = Subject.toEntity(dto);
        subject.setBranch(branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
        subjectRepository.save(subject);
        return new ApiResponse(SUCCESSFULLY, true, subject);
    }

    @Override
    public ApiResponse getById(Integer subjectId) {
        return null;
    }

    public ApiResponse getById(Integer subjectId, Integer levelId) {
        Subject subject = checkById(subjectId);
        List<TopicResponse> topicResponses = topicService.findALLBySubjectId(subject.getId(), levelId);
        return new ApiResponse(SUCCESSFULLY, true, new SubjectResponse(subject, topicResponses));
    }

    @Override
    public ApiResponse update(SubjectRequestDto subjectRequestDto) {
        Subject subject = checkById(subjectRequestDto.getId());
        subject.setName(subjectRequestDto.getName());
        subject.setBranch(branchRepository.findByIdAndDeleteFalse(subjectRequestDto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND)));
        subjectRepository.save(subject);
        return new ApiResponse(SUCCESSFULLY, true, subject);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Subject subject = checkById(id);
        subject.setActive(false);
        subjectRepository.save(subject);
        return new ApiResponse(DELETED, true, subject);
    }

    public ApiResponse getAllSubjectByBranchId(Integer branchId) {
        return new ApiResponse(subjectRepository.findAllByBranchId(branchId, Sort.by(Sort.Direction.DESC, "id")), true);
    }

    public Subject checkById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
    }
}
