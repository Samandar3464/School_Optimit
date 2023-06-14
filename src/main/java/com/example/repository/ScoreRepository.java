package com.example.repository;

import com.example.entity.Attendance;
import com.example.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    Page<Score> findAllByJournalIdAndTeacherIdAndSubjectId(Integer journalId,Integer teacherId,Integer subjectId, Pageable pageable);
    List<Score> findAllByJournalIdAndTeacherIdAndSubjectIdAndCreatedDateBetween(Integer journalId, Integer teacherId, Integer subjectId, LocalDateTime startWeek, LocalDateTime endWeek);

    List<Score> findAllByJournalId(Integer journalId);
}
