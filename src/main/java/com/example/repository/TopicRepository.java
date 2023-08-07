package com.example.repository;

import com.example.entity.Topic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Boolean existsByNameAndSubjectIdAndLevelId(String name, Integer subjectId,Integer levelId);

    List<Topic> findAllBySubjectIdAndLevelId(Integer subjectId, Integer levelId, Sort sort);
}
