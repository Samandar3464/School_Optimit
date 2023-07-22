package com.example.service;

import com.example.entity.*;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentClassDto;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentClassService implements BaseService<StudentClassDto, Integer> {

    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(StudentClassDto studentClass) {
        Branch branch = branchRepository.findById(studentClass.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Room room = roomRepository.findById(studentClass.getRoomId())
                .orElseThrow(() -> new RecordNotFoundException(ROOM_NOT_FOUND));
        User teacher = userRepository.findById(studentClass.getClassLeaderId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Level level = levelRepository.findById(studentClass.getLevelId())
                .orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
        StudentClass from = StudentClass.from(studentClass);
        from.setBranch(branch);
        from.setRoom(room);
        from.setClassLeader(teacher);
        from.setLevel(level);
        studentClassRepository.save(from);
        return new ApiResponse(SUCCESSFULLY, true);
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        return new ApiResponse(studentClass, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse update(StudentClassDto studentClassDto) {
        StudentClass studentClass = studentClassRepository.findById(studentClassDto.getId())
                .orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        if (studentClassDto.getRoomId() != null) {
            Room room = roomRepository.findById(studentClassDto.getRoomId())
                    .orElseThrow(() -> new RecordNotFoundException(ROOM_NOT_FOUND));
            studentClass.setRoom(room);
        }
        if (studentClassDto.getClassLeaderId() != null) {
            User teacher = userRepository.findById(studentClassDto.getClassLeaderId())
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
            studentClass.setClassLeader(teacher);
        }
        if (studentClassDto.getLevelId() != null) {
            Level level = levelRepository.findById(studentClassDto.getLevelId())
                    .orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
            studentClass.setLevel(level);
        }
        studentClass.setClassName(studentClassDto.getClassName());
        studentClass.setStartDate(studentClassDto.getStartDate());
        studentClass.setEndDate(studentClassDto.getEndDate());
        studentClassRepository.save(studentClass);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse delete(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        studentClass.setActive(false);
        studentClassRepository.save(studentClass);
        return new ApiResponse(DELETED, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllActiveClasses(Integer branchId) {
        List<StudentClass> allByActiveTrue = studentClassRepository.findAllByActiveTrueAndBranchId(branchId);
        return new ApiResponse(allByActiveTrue, true);
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllNeActiveClassesByYear(LocalDate startDate, LocalDate endDate, int id) {
        List<StudentClass> allByStartDateAfterAndEndDateBeforeAndActiveFalse = studentClassRepository.findAllByBranchIdAndStartDateAfterAndEndDateBeforeAndActiveFalse(id, startDate, endDate);
        return new ApiResponse(allByStartDateAfterAndEndDateBeforeAndActiveFalse, true);
    }
}
