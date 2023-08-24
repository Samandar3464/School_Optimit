package com.example.repository;

import com.example.entity.Topic;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Boolean existsByNameAndSubjectLevelId(String name, Integer subjectLevelId);

    List<Topic> findAllBySubjectLevelId(Integer subjectLevelId, Sort sort);
}
