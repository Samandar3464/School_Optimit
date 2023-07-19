package com.example.service;

import com.example.entity.Attachment;
import com.example.entity.Subject;
import com.example.entity.Topic;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TopicRequestDto;
import com.example.model.response.TopicResponseDto;
import com.example.repository.SubjectRepository;
import com.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TopicService implements BaseService<TopicRequestDto, UUID> {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final AttachmentService attachmentService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(TopicRequestDto dto) {
        if (topicRepository.existsByNameAndSubjectId(dto.getName(), dto.getSubjectId())) {
            throw new RecordAlreadyExistException(TOPIC_ALREADY_EXIST);
        }
        StringBuilder useFullLinks = new StringBuilder("");
        dto.getUseFullLinks().forEach(obj -> useFullLinks.append(obj).append("newLink"));
        Subject subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        List<Attachment> files = attachmentService.saveToSystemListFile(dto.getLessonFiles());
        Topic topic = from(dto, useFullLinks, subject, files);
        topicRepository.save(topic);
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(UUID id) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        String[] newLinks = topic.getUseFullLinks().split("newLink");
        TopicResponseDto topicResponseDto = TopicResponseDto.from(topic, attachmentService.getUrlList(topic.getLessonFiles()), newLinks);
        return new ApiResponse(topicResponseDto, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(TopicRequestDto dto) {
        Topic oldTopic = topicRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        if ((!oldTopic.getName().equals(dto.getName()) || !dto.getSubjectId().equals(oldTopic.getSubject().getId()))
                && topicRepository.existsByNameAndSubjectId(dto.getName(), dto.getSubjectId())) {
            throw new RecordAlreadyExistException(TOPIC_ALREADY_EXIST);
        }
        StringBuilder useFullLinks = new StringBuilder(oldTopic.getUseFullLinks());
        Subject subject = oldTopic.getSubject();
        List<Attachment> files = oldTopic.getLessonFiles();
        if (dto.getUseFullLinks() != null) {
            dto.getUseFullLinks().forEach(obj -> useFullLinks.append(obj).append("newLink"));
        }
        if (dto.getSubjectId() != null) {
            subject = subjectRepository.findById(dto.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        }
        if (dto.getLessonFiles() != null) {
            files = attachmentService.saveToSystemListFile(dto.getLessonFiles());
            oldTopic.getLessonFiles().forEach(attachmentService::deleteNewName);
        }
        Topic topic = from(dto, useFullLinks, subject, files);
        topic.setId(dto.getId());
        topicRepository.save(topic);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(UUID id) {
        Topic oldTopic = topicRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        for (Attachment lessonFile : oldTopic.getLessonFiles()) {
            attachmentService.deleteNewName(lessonFile);
        }
        topicRepository.deleteById(id);
        return new ApiResponse(DELETED, true);
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteALLBySubjectId(Integer subjectId) {
        List<Topic> allBySubjectId = topicRepository.findAllBySubjectId(subjectId);
        allBySubjectId.forEach(topic -> {
            topic.getLessonFiles().forEach(attachmentService::deleteNewName);
            topicRepository.deleteById(topic.getId());
        });
        return true;
    }

    public List<TopicResponseDto> findALLBySubjectId(Integer subjectId) {
        List<Topic> allBySubjectId = topicRepository.findAllBySubjectId(subjectId);
        List<TopicResponseDto> topicResponseDtoList = new ArrayList<>();
        allBySubjectId.forEach(topic -> {
            topicResponseDtoList.add(TopicResponseDto.from(topic, attachmentService.getUrlList(topic.getLessonFiles()), topic.getUseFullLinks().split("newLink")));
        });
        return topicResponseDtoList;
    }

    private static Topic from(TopicRequestDto dto, StringBuilder useFullLinks, Subject subject, List<Attachment> files) {
        return Topic.builder()
                .name(dto.getName())
                .useFullLinks(useFullLinks.toString())
                .lessonFiles(files)
                .subject(subject)
                .creationDate(LocalDateTime.now())
                .build();
    }
}
