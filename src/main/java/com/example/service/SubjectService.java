package com.example.service;

import com.example.entity.Subject;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequest;
import com.example.model.response.SubjectResponse;
import com.example.model.response.TopicResponseDto;
import com.example.repository.BranchRepository;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService implements BaseService<SubjectRequest, Integer> {

    private final SubjectRepository subjectRepository;
    private final BranchRepository branchRepository;
    private final TopicService topicService;

    @Override
    public ApiResponse create(SubjectRequest subjectRequest) {
        checkIfExist(subjectRequest);
        Subject subject = Subject.toSubject(subjectRequest);
        subject.setBranch(branchRepository.findById(subjectRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
        subjectRepository.save(subject);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        return new ApiResponse(checkById(id), true);
    }

    public ApiResponse getById(Integer subjectId, Integer levelId) {
        Subject subject = checkById(subjectId);
        List<TopicResponseDto> all = topicService.findAllBySubjectIdAndLevelId(subjectId, levelId);
        return new ApiResponse(new SubjectResponse(subject, all), true);
    }

    @Override
    public ApiResponse update(SubjectRequest subjectRequest) {
        Subject subject = checkById(subjectRequest.getId());
        subject.setId(subjectRequest.getId());
        subject.setName(subjectRequest.getName());
        subject.setBranch(branchRepository.findById(subjectRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
        subjectRepository.save(subject);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Subject subject = checkById(id);
        subject.setActive(false);
        subjectRepository.save(subject);
        return new ApiResponse(Constants.DELETED, true, subject);
    }

    public ApiResponse getAllSubjectByBranchId(Integer branchId) {
        return new ApiResponse(subjectRepository.findAllByBranch_Id(branchId), true);
    }

    private void checkIfExist(SubjectRequest subjectRequest) {
        boolean present = subjectRepository.findByNameAndActiveTrue(subjectRequest.getName()).isPresent();
        if (present) {
            throw new RecordAlreadyExistException(Constants.SUBJECT_ALREADY_EXIST);
        }
    }

    public Subject checkById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND));
    }
}
