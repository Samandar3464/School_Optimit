package com.example.service;

import com.example.entity.Branch;
import com.example.entity.StaffAttendance;
import com.example.entity.User;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StaffAttendanceDto;
import com.example.repository.BranchRepository;
import com.example.repository.StaffAttendanceRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class StaffAttendanceService implements BaseService<StaffAttendanceDto, Integer> {

    private final StaffAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public ApiResponse create(StaffAttendanceDto dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        Branch branch = branchRepository.findById(dto.getBranchId()).orElseThrow(() -> new RecordNotFoundException(BRANCH_NOT_FOUND));
        StaffAttendance staffAttendance = StaffAttendance.from(dto, user, branch);
        attendanceRepository.save(staffAttendance);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer id) {
        StaffAttendance staffAttendance = attendanceRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(STAFF_ATTENDANCE_NOT_FOUND));
        return new ApiResponse(StaffAttendanceDto.from(staffAttendance), true);
    }

    public ApiResponse getAllByUserId(Integer userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<StaffAttendanceDto> staffAttendanceDtoList = new ArrayList<>();
        attendanceRepository.findAllByUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(userId, startTime, endTime).forEach(staffAttendance -> {
            staffAttendanceDtoList.add(StaffAttendanceDto.from(staffAttendance));
        });
        return new ApiResponse(staffAttendanceDtoList, true);
    }

    public ApiResponse getAllBranchId(Integer branchId, LocalDateTime startTime, LocalDateTime endTime) {
        List<StaffAttendanceDto> staffAttendanceDtoList = new ArrayList<>();
        attendanceRepository.findAllByBranchIdAndCreatedDateBetweenOrderByCreatedDateDesc(branchId, startTime, endTime).forEach(staffAttendance -> {
            staffAttendanceDtoList.add(StaffAttendanceDto.from(staffAttendance));
        });
        return new ApiResponse(SUCCESSFULLY, true, staffAttendanceDtoList);
    }

    @Override
    public ApiResponse update(StaffAttendanceDto dto) {
        StaffAttendance old = attendanceRepository.findById(dto.getId()).orElseThrow(() -> new RecordNotFoundException(STAFF_ATTENDANCE_NOT_FOUND));
        StaffAttendance staffAttendance = StaffAttendance.from(dto,old.getUser(),old.getBranch());
        staffAttendance.setId(dto.getId());
        attendanceRepository.save(staffAttendance);
        return new ApiResponse(SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        attendanceRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(STAFF_ATTENDANCE_NOT_FOUND));
        attendanceRepository.deleteById(integer);
        return new ApiResponse(SUCCESSFULLY, true);
    }

}