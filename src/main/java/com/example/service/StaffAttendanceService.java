package com.example.service;

import com.example.entity.StaffAttendance;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StaffAttendanceRequest;
import com.example.model.response.StaffAttendanceResponse;
import com.example.model.response.UserResponseDto;
import com.example.repository.StaffAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAttendanceService implements BaseService<StaffAttendanceRequest, Integer> {

    private final StaffAttendanceRepository attendanceRepository;
    private final UserService userService;


    @Override
    public ApiResponse create(StaffAttendanceRequest staffAttendanceRequest) {
        StaffAttendance staffAttendance = StaffAttendance.toStaffAttendance(staffAttendanceRequest);
        staffAttendance.setUser(userService.checkUserExistById(staffAttendanceRequest.getUserId()));
        staffAttendance.setDate(userService.toLocalDate(staffAttendanceRequest.getDate()));
        attendanceRepository.save(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toDto(staffAttendance));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(Constants.FOUND, true, checkById(integer));
    }

    public ApiResponse getAll() {
        List<StaffAttendanceResponse> all = new ArrayList<>();
        attendanceRepository.findAll().forEach(staffAttendance -> {
            all.add(toDto(staffAttendance));
        });
        return new ApiResponse(Constants.FOUND, true, all);
    }

    @Override
    public ApiResponse update(StaffAttendanceRequest staffAttendanceRequest) {
        checkById(staffAttendanceRequest.getId());
        StaffAttendance staffAttendance = StaffAttendance.toStaffAttendance(staffAttendanceRequest);
        staffAttendance.setId(staffAttendanceRequest.getId());
        staffAttendance.setDate(userService.toLocalDate(staffAttendanceRequest.getDate()));
        staffAttendance.setUser(userService.checkUserExistById(staffAttendanceRequest.getUserId()));
        attendanceRepository.save(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toDto(staffAttendance));
    }


    @Override
    public ApiResponse delete(Integer integer) {
        StaffAttendance staffAttendance = checkById(integer);
        attendanceRepository.deleteById(integer);
        return new ApiResponse(Constants.DELETED, true, staffAttendance);
    }


    public StaffAttendance checkById(Integer integer) {
        return attendanceRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
    }


    public StaffAttendanceResponse toDto(StaffAttendance staffAttendance) {
        return StaffAttendanceResponse
                .builder()
                .id(staffAttendance.getId())
                .cameToWork(staffAttendance.isCameToWork())
                .date(staffAttendance.getDate())
                .description(staffAttendance.getDescription())
                .userResponseDto(UserResponseDto.from(staffAttendance.getUser()))
                .build();
    }

    public List<StaffAttendance> findAllByUserAndDateBetween(LocalDate fromDate1, LocalDate toDate1, User user) {
        return attendanceRepository.findAllByUserAndDateBetweenAndCameToWorkTrue(user,fromDate1, toDate1);
    }
}