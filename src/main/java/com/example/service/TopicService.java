package com.example.service;

import com.example.entity.*;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TopicRequest;
import com.example.model.response.TopicResponse;
<<<<<<< HEAD
import com.example.repository.*;
import jakarta.persistence.Id;
=======
import com.example.repository.LevelRepository;
import com.example.repository.SubjectLevelRepository;
import com.example.repository.SubjectRepository;
import com.example.repository.TopicRepository;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TopicService implements BaseService<TopicRequest, Integer> {

    private final TopicRepository topicRepository;
    private final SubjectLevelRepository subjectLevelRepository;
<<<<<<< HEAD
    private final ModelMapper modelMapper;
    private final AttachmentRepository attachmentRepository;
=======
    private final AttachmentService attachmentService;
    private final ModelMapper modelMapper;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    @Override
    public ApiResponse create(TopicRequest dto) {
        if (topicRepository.existsByNameAndSubjectLevelId(dto.getName(), dto.getSubjectLevelId())) {
            throw new RecordAlreadyExistException(TOPIC_ALREADY_EXIST);
        }
        Topic topic = modelMapper.map(dto, Topic.class);
        setTopic(dto, topic);
        topicRepository.save(topic);
        TopicResponse response = getTopicResponse(topic);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        TopicResponse topicResponse = getTopicResponse(topic);
        return new ApiResponse(SUCCESSFULLY, true, topicResponse);
    }

    @Override
    public ApiResponse update(TopicRequest dto) {
        checkingTopicByExists(dto);
        Topic topic = modelMapper.map(dto, Topic.class);
        topic.setId(dto.getId());
        setTopic(dto, topic);
        topicRepository.save(topic);
        TopicResponse topicResponse = getTopicResponse(topic);
        return new ApiResponse(SUCCESSFULLY, true, topicResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse delete(Integer id) {
        Topic oldTopic = topicRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        topicRepository.deleteById(id);
<<<<<<< HEAD
        attachmentRepository.deleteAll(oldTopic.getLessonFiles());
=======
        oldTopic.getLessonFiles().forEach(attachmentService::deleteNewName);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        return new ApiResponse(DELETED, true);
    }

    public ApiResponse findAllByBranchId(Integer branchId) {
        List<Topic> all = topicRepository.findAllBySubjectLevelBranchId(branchId,
                Sort.by(Sort.Direction.DESC, "id"));
        List<TopicResponse> responses = new ArrayList<>();
        all.forEach(topic -> {
            responses.add(getTopicResponse(topic));
        });
        return new ApiResponse(SUCCESSFULLY, true, responses);
    }

    public ApiResponse findAllBySubjectIdBranchId(Integer subjectLevelId, Integer branchId) {
        List<Topic> all = topicRepository.findAllBySubjectLevelIdAndSubjectLevelBranchId(
                subjectLevelId, branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<TopicResponse> responses = new ArrayList<>();
        all.forEach(topic -> {
            responses.add(getTopicResponse(topic));
        });
        return new ApiResponse(SUCCESSFULLY, true, responses);
    }

    private void setTopic(TopicRequest dto, Topic topic) {
        SubjectLevel subjectLevel = subjectLevelRepository.findById(dto.getSubjectLevelId())
                .orElseThrow(() -> new RecordNotFoundException(SUBJECT_LEVEL_NOT_FOUND));
<<<<<<< HEAD
        if (dto.getLessonFilesIds() != null) {
            List<Attachment> attachmentList = attachmentRepository.findAllById(dto.getLessonFilesIds());
            topic.setLessonFiles(attachmentList);
=======
        if (dto.getLessonFiles() != null) {
            List<Attachment> attachments =
                    attachmentService.saveToSystemListFile(dto.getLessonFiles());
            topic.setLessonFiles(attachments);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        }

        topic.setCreationDate(LocalDateTime.now());
        topic.setSubjectLevel(subjectLevel);
    }

    private void checkingTopicByExists(TopicRequest dto) {
        Topic old = topicRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        if (!dto.getName().equals(old.getName()) || !dto.getSubjectLevelId().equals(old.getSubjectLevel().getId())) {
            if (topicRepository.existsByNameAndSubjectLevelId(dto.getName(), dto.getSubjectLevelId())) {
                throw new RecordNotFoundException(TOPIC_ALREADY_EXIST);
            }
        }
    }

    private TopicResponse getTopicResponse(Topic topic) {
        TopicResponse response = new TopicResponse();
        response.setName(topic.getName());
        response.setId(topic.getId());
        response.setSubjectLevel(topic.getSubjectLevel());
        response.setUseFullLinks(topic.getUseFullLinks() == null ? null : topic.getUseFullLinks());
<<<<<<< HEAD
        if (response.getLessonFilesId() != null) {
           List<Integer> integerList = new ArrayList<>();
            List<Attachment> lessonFiles = topic.getLessonFiles();
            for (Attachment lessonFile : lessonFiles) {
                integerList.add(lessonFile.getId());
            }
            response.setLessonFilesId(integerList);
=======
        if (response.getLessonFiles() != null) {
            List<String> urlList = attachmentService.getUrlList(topic.getLessonFiles());
            response.setLessonFiles(urlList);
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348
        }
        return response;
    }
}
