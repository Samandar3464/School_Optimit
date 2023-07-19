package com.example.repository;

import com.example.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    Boolean existsByNameAndSubjectId(String name, Integer subjectId);

    List<Topic> findAllBySubjectId(Integer subjectId);
}
