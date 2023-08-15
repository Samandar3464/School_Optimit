package com.example.service;

import com.example.entity.Attachment;
import com.example.entity.Level;
import com.example.entity.Subject;
import com.example.entity.Topic;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TopicRequest;
import com.example.model.response.TopicResponse;
import com.example.repository.LevelRepository;
import com.example.repository.SubjectRepository;
import com.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class TopicService implements BaseService<TopicRequest, Integer> {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final AttachmentService attachmentService;
    private final LevelRepository levelRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse create(TopicRequest dto) {
        if (topicRepository.existsByNameAndSubjectIdAndLevelId(dto.getName(), dto.getSubjectId(), dto.getLevelId())) {
            throw new RecordAlreadyExistException(TOPIC_ALREADY_EXIST);
        }
        Topic topic = modelMapper.map(dto,Topic.class);
        setTopic(dto, topic);
        topicRepository.save(topic);
        TopicResponse response = getTopicResponse(topic);
        return new ApiResponse(SUCCESSFULLY, true,response);
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
        Topic topic = modelMapper.map(dto,Topic.class);
        topic.setId(dto.getId());
        setTopic(dto, topic);
        topicRepository.save(topic);
        TopicResponse topicResponse = getTopicResponse(topic);
        return new ApiResponse(SUCCESSFULLY, true, topicResponse);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Topic oldTopic = topicRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        oldTopic.getLessonFiles().forEach(attachmentService::deleteNewName);
        topicRepository.deleteById(id);
        return new ApiResponse(DELETED, true, getTopicResponse(oldTopic));
    }

    public List<TopicResponse> findAllBySubjectId(Integer subjectId, Integer levelId) {
        List<Topic> all = topicRepository.findAllBySubjectIdAndLevelId(
                subjectId,levelId,
                Sort.by(Sort.Direction.DESC, "id"));
        List<TopicResponse> responses = new ArrayList<>();
        all.forEach(topic -> {
            responses.add(getTopicResponse(topic));
        });
        return responses;
    }

    private void setTopic(TopicRequest dto, Topic topic) {
        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RecordNotFoundException(SUBJECT_NOT_FOUND));
        List<Attachment> attachments =
                attachmentService.saveToSystemListFile(dto.getLessonFiles());

        topic.setCreationDate(LocalDateTime.now());
        topic.setLevel(level);
        topic.setSubject(subject);
        topic.setLessonFiles(attachments);
    }

    private void checkingTopicByExists(TopicRequest dto) {
        Topic old = topicRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(TOPIC_NOT_FOUND));
        if (!dto.getName().equals(old.getName()) || !dto.getSubjectId().equals(old.getSubject().getId()) || !dto.getLevelId().equals(old.getLevel().getId())
                && topicRepository.existsByNameAndSubjectIdAndLevelId(dto.getName(), dto.getSubjectId(), dto.getLevelId())) {
            throw new RecordNotFoundException(TOPIC_ALREADY_EXIST);
        }
    }

    private TopicResponse getTopicResponse(Topic topic) {
        TopicResponse response = modelMapper.map(topic, TopicResponse.class);
        List<String> urlList = attachmentService.getUrlList(topic.getLessonFiles());
        response.setLessonFiles(urlList);
        return response;
    }
}
