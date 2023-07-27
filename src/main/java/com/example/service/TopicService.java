package com.example.service;

import com.example.entity.Attachment;
import com.example.entity.Topic;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TopicRequest;
import com.example.model.response.TopicResponseDto;
import com.example.repository.LevelRepository;
import com.example.repository.SubjectRepository;
import com.example.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService implements BaseService<TopicRequest, Integer> {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final LevelRepository levelRepository;
    private final AttachmentService attachmentService;

    @Override
    public ApiResponse create(TopicRequest topicRequest) {
        if (topicRepository.existsByNameAndSubjectId(topicRequest.getName(), topicRequest.getSubjectId())) {
            throw new RecordAlreadyExistException(Constants.TOPIC_ALREADY_EXIST);
        }
        setTopic(topicRequest, new Topic());
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        Topic topic = checkById(id);
        String[] newLinks = topic.getUseFullLinks().split("newLink");
        TopicResponseDto topicResponseDto = TopicResponseDto.from(topic, attachmentService.getUrlList(topic.getLessonFiles()), newLinks);
        return new ApiResponse(Constants.SUCCESSFULLY, true, topicResponseDto);
    }

    @Override
    public ApiResponse update(TopicRequest topicRequest) {
        Topic topic = checkById(topicRequest.getId());
        topic.getLessonFiles().forEach(attachmentService::deleteNewName);
        setTopic(topicRequest, topic);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    private void setTopic(TopicRequest topicRequest, Topic topic) {
        topic.setName(topicRequest.getName());
        topic.setCreationDate(LocalDate.now());
        topic.setSubject(subjectRepository.findById(topicRequest.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
        topic.setLevel(levelRepository.findById(topicRequest.getLevelId()).orElseThrow(() -> new RecordNotFoundException(Constants.LEVEL_NOT_FOUND)));
        topic.setLessonFiles(attachmentService.saveToSystemListFile(topicRequest.getLessonFiles()));
        StringBuilder links = new StringBuilder("");
        topicRequest.getUseFullLinks().forEach(link -> links.append(link).append("newLink"));
        topic.setUseFullLinks(String.valueOf(links));
        topicRepository.save(topic);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Topic topic = checkById(id);
        topic.getLessonFiles().forEach(attachmentService::deleteNewName);
        topicRepository.deleteById(id);
        return new ApiResponse(Constants.DELETED, true);
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteALLBySubjectIdAndLevelId(Integer subjectId, Integer levelId) {
        List<Topic> allBySubjectId = topicRepository.findAllBySubjectIdAndLevelId(subjectId,levelId);
        allBySubjectId.forEach(topic -> {
            topic.getLessonFiles().forEach(attachmentService::deleteNewName);
            topicRepository.deleteById(topic.getId());
        });
        return true;
    }

    public Topic checkById(Integer id) {
        return topicRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.TOPIC_NOT_FOUND));
    }

    public List<TopicResponseDto> findAllBySubjectIdAndLevelId(Integer subjectId, Integer levelId) {
        List<TopicResponseDto> topicResponseDtoList = new ArrayList<>();
        topicRepository.findAllBySubjectIdAndLevelId(subjectId, levelId).forEach(topic -> {
            topicResponseDtoList.add(TopicResponseDto.from(topic, attachmentService.getUrlList(topic.getLessonFiles()), topic.getUseFullLinks().split("newLink")));
        });
        return topicResponseDtoList;
    }
}
