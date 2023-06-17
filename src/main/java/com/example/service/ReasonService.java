package com.example.service;

import com.example.entity.*;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.ReasonRequestDto;
import com.example.model.response.ReasonResponse;
import com.example.repository.BranchRepository;
import com.example.repository.ReasonRepository;
import com.example.repository.ScoreRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class ReasonService implements BaseService<ReasonRequestDto, Integer> {

    private final ReasonRepository reasonRepository;
    private final StudentRepository studentRepository;
    private final AttachmentService attachmentService;
    private final BranchRepository branchRepository;
    private final ScoreRepository scoreRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(ReasonRequestDto dto) {
        Student student = studentRepository.findById(dto.getStudentId()).orElseThrow(() -> new UserNotFoundException(STUDENT_NOT_FOUND));
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Attachment attachment = attachmentService.saveToSystem(dto.getImage());
        Reason reason = Reason.builder()
                .branch(branch)
                .student(student)
                .image(attachment)
                .reason(dto.getReason())
                .days(dto.getDays())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .createDate(LocalDateTime.now())
                .active(true)
                .build();
        reasonRepository.save(reason);
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        LocalDateTime start = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), 00, 00);
        LocalDateTime end = LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), 23, 59);
        List<Score> all = scoreRepository.findAllByStudentIdAndCreatedDateBetween(student.getId(), start, end);
        all.forEach(score -> score.setScore('s'));
        scoreRepository.saveAll(all);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        Reason reason = reasonRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(REASON_NOT_FOUND));
        ReasonResponse reasonResponse = ReasonResponse.from(reason, attachmentService.getUrl(reason.getImage()));
        return new ApiResponse(reasonResponse, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(ReasonRequestDto reasonRequestDto) {
        return null;
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        Reason reason = reasonRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(REASON_NOT_FOUND));
        reason.setActive(false);
        reasonRepository.save(reason);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByStudentId(Integer integer) {
        List<Reason> reasonList = reasonRepository.findAllByStudentIdAndActiveTrueOrderByCreateDateAsc(integer);
        List<ReasonResponse> reasonResponseList = new ArrayList<>();
        reasonList.forEach(reason -> reasonResponseList.add(ReasonResponse.from(reason, attachmentService.getUrl(reason.getImage()))));
        return new ApiResponse(reasonResponseList, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getByBranchId(Integer integer) {
        List<Reason> reasonList = reasonRepository.findAllByBranchIdAndActiveTrueOrderByCreateDateAsc(integer);
        List<ReasonResponse> reasonResponseList = new ArrayList<>();
        reasonList.forEach(reason -> reasonResponseList.add(ReasonResponse.from(reason, attachmentService.getUrl(reason.getImage()))));
        return new ApiResponse(reasonResponseList, true);
    }
}
