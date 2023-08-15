package com.example.repository;

import com.example.entity.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Integer> {

    Page<Score> findAllByJournalIdAndTeacherIdAndSubjectId(Integer journalId, Integer teacherId, Integer subjectId, Pageable pageable);

    List<Score> findAllByJournalIdAndTeacherIdAndSubjectIdAndCreatedDateBetween(Integer journalId, Integer teacherId, Integer subjectId, LocalDateTime startWeek, LocalDateTime endWeek,Sort sort);

    Page<Score> findAllByJournalId(Integer journalId,Pageable pageable);
    List<Score> findAllByStudentIdAndCreatedDateBetween(Integer studentId, LocalDateTime createdDate, LocalDateTime createdDate2,Sort sort);

    Page<Score> findAllByJournalIdAndSubjectIdAndStudentId(Integer journalId, Integer subjectId, Integer studentId, Pageable pageable);

    List<Score> findAllByCreatedDateBetween(LocalDateTime createdDate, LocalDateTime createdDate2,Sort sort);

    List<Score> findAllByJournalIdAndSubjectIdAndAndCreatedDateBetween(Integer journalId, Integer subjectId, LocalDateTime startWeek, LocalDateTime endWeek, Sort id);

    Page<Score> findAllByStudentId(Integer studentId,Pageable pageable);

    Page<Score> findAllByStudentIdAndSubjectId(Integer studentId, Integer subjectId, PageRequest id);
}
