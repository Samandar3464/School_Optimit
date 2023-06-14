package com.example.service;

import com.example.entity.Attendance;
import com.example.entity.Journal;
import com.example.entity.Student;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.AttendanceRequestDto;
import com.example.model.request.ScoreDto;
import com.example.repository.AttendanceRepository;
import com.example.repository.JournalRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class AttendanceService implements BaseService<AttendanceRequestDto, UUID> {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final JournalRepository journalRepository;

    @Override
    public ApiResponse create(AttendanceRequestDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        Journal journal = journalRepository.findById(dto.getJournalId())
                .orElseThrow(() -> new UserNotFoundException(JOURNAL_NOT_FOUND));
        Attendance attendance = Attendance.builder()
                .createdDate(LocalDateTime.now())
                .student(student)
                .journal(journal)
                .come(true)
                .build();
        attendanceRepository.save(attendance);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(UUID uuid) {
        return null;
    }

    @Override
    public ApiResponse update(AttendanceRequestDto dto) {
        Attendance attendance = attendanceRepository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundException(ATTENDANT_NOT_FOUND));
        attendance.setCome(false);
        attendance.setUpdateDate(LocalDateTime.now());
        attendanceRepository.save(attendance);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(UUID uuid) {
        return null;
    }

    public ApiResponse getAll(Integer journalId) {
        int dayOfWeek = LocalDate.now().atStartOfDay().getDayOfWeek().getValue();
        LocalDateTime startWeek = LocalDateTime.now().minusDays(dayOfWeek);
        LocalDateTime endWeek = startWeek.plusDays(7);
        List<Attendance> all = attendanceRepository.findAllByJournalIdAndCreatedDateBetween(journalId, startWeek, endWeek);
        return new ApiResponse(all, true);
    }
}
