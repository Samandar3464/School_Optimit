package com.example.repository;

import com.example.entity.Topic;
import com.example.model.response.TopicResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic,Integer> {

    boolean existsByNameAndSubjectId(String name, Integer subject_id);

    List<Topic> findAllBySubjectIdAndLevelId(Integer subjectId, Integer levelId);
}
