package com.example.service;

import com.example.entity.Subject;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequestDto;
import com.example.model.response.SubjectResponse;
import com.example.model.response.TopicResponseDto;
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

    @Override
    public ApiResponse create(SubjectRequestDto dto) {
        if (subjectRepository.findByNameAndLevel(dto.getName(), dto.getLevel()).isPresent()) {
            throw new RecordAlreadyExistException(SUBJECT_ALREADY_EXIST);
        }
        Subject subject = Subject.from(dto);
        subjectRepository.save(subject);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        Subject subject = checkById(id);
        List<TopicResponseDto> allBySubjectId = topicService.findALLBySubjectId(subject.getId());
        return new ApiResponse(new SubjectResponse(subject, allBySubjectId), true);
    }

    @Override
    public ApiResponse update(SubjectRequestDto subjectRequestDto) {
        checkById(subjectRequestDto.getId());
        Subject subject = Subject.from(subjectRequestDto);
        subject.setId(subjectRequestDto.getId());
        subjectRepository.save(subject);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse delete(Integer id) {
        Subject subject = checkById(id);
        if (topicService.deleteALLBySubjectId(subject.getId())) {
            subjectRepository.deleteById(id);
            return new ApiResponse(DELETED, true);
        } else {
            return new ApiResponse(CAN_NOT_DELETED, false);
        }
    }

    public Subject checkById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
    }

    public List<Subject> checkAllById(List<Integer> subjects) {
        return subjectRepository.findAllById(subjects);
    }
}
