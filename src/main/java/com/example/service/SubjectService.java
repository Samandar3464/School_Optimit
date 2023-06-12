package com.example.service;

import com.example.entity.Subject;
import com.example.entity.Topic;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SubjectRequest;
import com.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService implements BaseService<SubjectRequest, Integer> {

    private final SubjectRepository subjectRepository;
    private final TopicService topicService;

    @Override
    public ApiResponse create(SubjectRequest subjectRequest) {
        checkIfExist(subjectRequest);
        Subject subject = Subject.toSubject(subjectRequest);
        Subject save = subjectRepository.save(subject);
        return new ApiResponse(save, true);
    }

    public ApiResponse addTopic(SubjectRequest subjectRequest) {
        Subject subject = checkById(subjectRequest.getId());
        setTopic(subjectRequest, subject);
        subject = subjectRepository.save(subject);
        return new ApiResponse(subject, true);
    }

    public ApiResponse getTopicList(Integer subjectId) {
        List<Topic> topicList = checkById(subjectId).getTopicList();
        return new ApiResponse(topicList,true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        Subject subject = checkById(id);
        return new ApiResponse(subject, true);
    }

    @Override
    public ApiResponse update(SubjectRequest subjectRequest) {
        Subject checkById = checkById(subjectRequest.getId());
        Subject subject = Subject.toSubject(subjectRequest);
        subject.setId(checkById.getId());
        subject.setTopicList(topicService.checkAllById(subjectRequest.getTopicList()));
        subject = subjectRepository.save(subject);
        return new ApiResponse(subject, true);
    }

    @Override
    public ApiResponse delete(Integer id) {
        Subject subject = checkById(id);
        subjectRepository.deleteById(id);
        return new ApiResponse(subject, true);
    }


    public ApiResponse deleteTopic(Integer subjectId, Integer topicId) {
        Subject subject = checkById(subjectId);
        List<Topic> topicList = new ArrayList<>();
        topicDelete(topicId, subject, topicList);
        subject.setTopicList(topicList);
        subjectRepository.save(subject);
        return new ApiResponse(Constants.DELETED, true);
    }

    private void topicDelete(Integer topicId, Subject subject, List<Topic> topicList) {
        subject.getTopicList().forEach(topic -> {
            if (!topic.getId().equals(topicId)) {
                topicList.add(topic);
            }
        });
        if (subject.getTopicList().size() == topicList.size()) {
            throw new RecordNotFoundException(Constants.TOPIC_NOT_FOUND);
        }
    }

    private void setTopic(SubjectRequest subjectRequest, Subject subject) {
        List<Topic> topicList = new ArrayList<>();
        List<Integer> list = subjectRequest.getTopicList();
        for (Integer id : list) {
            Topic checkTopic = topicService.checkById(id);
            subject.getTopicList().forEach(topic -> {
                if (topic.getId().equals(checkTopic.getId())) {
                    throw new RecordAlreadyExistException(Constants.TOPIC_ALREADY_EXIST + "  " + checkTopic);
                }
            });
            topicList.add(checkTopic);
        }
        topicList.addAll(subject.getTopicList());
        subject.setTopicList(topicList);
    }

    //    public ApiResponse deleteTeacher(Integer subjectId,Integer teacherId) {
//        Subject subject = checkById(subjectId);
//        List<User> list = new ArrayList<>(subject.getTeachers().stream().toList());
//        for (User teacher : list) {
//            if (teacher.getId().equals(teacherId)){
//                list.remove(teacher);
//                subject.setTeachers(list);
//                subjectRepository.save(subject);
//                return new ApiResponse(Constants.DELETED, true,teacher);
//            }
//        }
//        throw new RecordNotFoundException(Constants.TOPIC_NOT_FOUND);
//    }

    //    public ApiResponse addTeacher(SubjectRequest subjectRequest) {
//        Subject subject = checkById(subjectRequest.getId());
//        setTeacher(subjectRequest, subject);
//        subject = subjectRepository.save(subject);
//        return new ApiResponse(subject, true);
//    }


//    private void setTeacher(SubjectRequest subjectRequest, Subject subject) {
//        List<User> userList = new ArrayList<>();
//        User user = userService.checkUserExistById(subjectRequest.getTeacherId());
//        subject.getTeachers().forEach(teacher -> {
//            if (teacher.getId().equals(user.getId())) {
//                throw new RecordAlreadyExistException(Constants.TEACHER_ALREADY_EXIST);
//            }
//        });
//        userList.add(user);
//        userList.addAll(subject.getTeachers());
//        subject.setTeachers(userList);
//    }

    private void checkIfExist(SubjectRequest subjectRequest) {
        boolean present = subjectRepository.findByName(subjectRequest.getName()).isPresent();
        boolean level = subjectRepository.findByLevel(subjectRequest.getLevel()).isPresent();
        if (present && level) {
            throw new RecordAlreadyExistException(Constants.SUBJECT_ALREADY_EXIST);
        }
    }

    public Subject checkById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND));
    }

    public List<Subject> checkAllById(List<Integer> subjects) {
        return subjectRepository.findAllById(subjects);
    }
}
