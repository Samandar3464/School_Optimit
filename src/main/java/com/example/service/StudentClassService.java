package com.example.service;

import com.example.entity.*;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentClassDto;
import com.example.model.response.StudentClassResponse;
import com.example.model.response.UserResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StudentClassService implements BaseService<StudentClassDto, Integer> {

    private final StudentClassRepository studentClassRepository;
    private final BranchRepository branchRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final ModelMapper modelMapper;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse create(StudentClassDto studentClassDto) {
        Branch branch = branchRepository.findById(studentClassDto.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        Room room = roomRepository.findById(studentClassDto.getRoomId())
                .orElseThrow(() -> new RecordNotFoundException(ROOM_NOT_FOUND));
        User teacher = userRepository.findById(studentClassDto.getClassLeaderId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Level level = levelRepository.findById(studentClassDto.getLevelId())
                .orElseThrow(() -> new RecordNotFoundException(LEVEL_NOT_FOUND));
        StudentClass studentClass = StudentClass.from(studentClassDto);
        studentClass.setBranch(branch);
        studentClass.setRoom(room);
        studentClass.setClassLeader(teacher);
        studentClass.setLevel(level);
        studentClassRepository.save(studentClass);
        StudentClassResponse response = getStudentClassResponse(studentClass);
        return new ApiResponse(SUCCESSFULLY, true, response);
    }

    private StudentClassResponse getStudentClassResponse(StudentClass studentClass) {
        StudentClassResponse response = modelMapper.map(studentClass, StudentClassResponse.class);
        response.setClassLeader(modelMapper.map(studentClass.getClassLeader(), UserResponse.class));
        response.setStartDate(studentClass.getStartDate().toString());
        response.setEndDate(studentClass.getEndDate().toString());
        return response;
    }


    @Override
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getById(Integer integer) {
        StudentClass studentClass = studentClassRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(CLASS_NOT_FOUND));
        return new ApiResponse(SUCCESSFULLY, true, getStudentClassResponse(studentClass));
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
        return new ApiResponse(SUCCESSFULLY, true, getStudentClassResponse(studentClass));
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
        List<StudentClass> allByActiveTrue = studentClassRepository.findAllByActiveTrueAndBranchId(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentClassResponse> allResponse = getStudentClassResponses(allByActiveTrue);
        return new ApiResponse(SUCCESSFULLY, true, allResponse);
    }

    private List<StudentClassResponse> getStudentClassResponses(List<StudentClass> allByActiveTrue) {
        List<StudentClassResponse> allResponse = new ArrayList<>();
        allByActiveTrue.forEach(studentClass -> {
            allResponse.add(getStudentClassResponse(studentClass));
        });
        return allResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getAllNeActiveClassesByYear(LocalDate startDate, LocalDate endDate, int id) {
        List<StudentClass> all = studentClassRepository.findAllByBranchIdAndStartDateAfterAndEndDateBeforeAndActiveFalse(id, startDate, endDate, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentClassResponse> responses = getStudentClassResponses(all);
        return new ApiResponse(SUCCESSFULLY, true,responses);
    }
}
