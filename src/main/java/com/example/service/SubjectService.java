package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Subject;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequestDto;
import com.example.model.response.SubjectResponse;
import com.example.model.response.TopicResponseDto;
import com.example.repository.BranchRepository;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        subjectRepository.save(Subject.from(dto, branch));
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer subjectId) {
        return null;
    }

    public ApiResponse getById(Integer subjectId, Integer levelId) {
        Subject subject = checkById(subjectId);
        List<TopicResponseDto> allBySubjectId = topicService.findALLBySubjectId(subject.getId(), levelId);
        return new ApiResponse(new SubjectResponse(subject, allBySubjectId), true);
    }

    @Override
    public ApiResponse update(SubjectRequestDto subjectRequestDto) {
        Subject subject = checkById(subjectRequestDto.getId());
        subject.setName(subjectRequestDto.getName());
        subjectRepository.save(subject);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Subject subject = checkById(id);
        subject.setActive(false);
        subjectRepository.save(subject);
        return new ApiResponse(DELETED, true);
    }

    public ApiResponse getAllSubjectByBranchId(Integer branchId) {
        return new ApiResponse(subjectRepository.findAllByBranchId(branchId), true);
    }

    public Subject checkById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
    }

    public List<Subject> checkAllById(List<Integer> subjects) {
        return subjectRepository.findAllById(subjects);
    }
}
