package com.example.servise;

import com.example.entity.StudentClass;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentClassService implements BaseService<StudentClass, Integer> {

    private final StudentClassRepository studentClassRepository;

    @Override
    public ApiResponse create(StudentClass studentClass) {
        StudentClass studentClass1 = StudentClass.builder()
                .className(studentClass.getClassName())
                .createdDate(LocalDateTime.now())
                .startDate(studentClass.getStartDate())
                .endDate(studentClass.getEndDate())
                .roomNumber(studentClass.getRoomNumber())
                .active(true)
                .build();
        studentClassRepository.save(studentClass1);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        return new ApiResponse(studentClass, true);
    }

    @Override
    public ApiResponse update(StudentClass studentClass) {
        studentClassRepository.findById(studentClass.getId()).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        StudentClass build = StudentClass.builder()
                .id(studentClass.getId())
                .className(studentClass.getClassName())
                .startDate(studentClass.getStartDate())
                .endDate(studentClass.getEndDate())
                .build();
        studentClassRepository.save(build);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        studentClass.setActive(false);
        studentClassRepository.save(studentClass);
        return new ApiResponse(DELETED, true);
    }
}
