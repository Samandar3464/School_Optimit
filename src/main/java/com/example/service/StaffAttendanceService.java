package com.example.service;

import com.example.entity.Salary;
import com.example.entity.StaffAttendance;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StaffAttendanceRequest;
import com.example.model.response.StaffAttendanceResponse;
import com.example.repository.BranchRepository;
import com.example.repository.SalaryRepository;
import com.example.repository.StaffAttendanceRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAttendanceService implements BaseService<StaffAttendanceRequest, Integer> {

    private final StaffAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final BranchRepository branchRepository;


    @Override
    public ApiResponse create(StaffAttendanceRequest staffAttendanceRequest) {
        StaffAttendance staffAttendance = StaffAttendance.toStaffAttendance(staffAttendanceRequest);
        if (attendanceRepository.findByUserIdAndDate(staffAttendanceRequest.getUserId(), staffAttendanceRequest.getDate()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.STAFF_ATTENDANCE_ALREADY_EXISTS_FOR_THIS_DATE);
        }
        setAndSave(staffAttendanceRequest, staffAttendance);
        dailyWageSetting(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        StaffAttendance staffAttendance = attendanceRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        return new ApiResponse(Constants.SUCCESSFULLY, true, StaffAttendanceResponse.toResponse(staffAttendance));
    }

    public ApiResponse getAllByUserId(Integer id) {
        List<StaffAttendanceResponse> all = StaffAttendanceResponse.toAllResponse(attendanceRepository.findAllByUserId(id));
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    public ApiResponse getAllByBranchId(Integer id) {
        List<StaffAttendanceResponse> all = StaffAttendanceResponse.toAllResponse(attendanceRepository.findAllByBranchId(id));
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    @Override
    public ApiResponse update(StaffAttendanceRequest staffAttendanceRequest) {
        attendanceRepository.findByUserIdAndDate(staffAttendanceRequest.getUserId(), staffAttendanceRequest.getOldDate()).orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        if (attendanceRepository.findByUserIdAndDate(staffAttendanceRequest.getUserId(), staffAttendanceRequest.getDate()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.STAFF_ATTENDANCE_ALREADY_EXISTS_FOR_THIS_DATE);
        }
        StaffAttendance staffAttendance = StaffAttendance.toStaffAttendance(staffAttendanceRequest);
        staffAttendance.setId(staffAttendanceRequest.getId());
        setAndSave(staffAttendanceRequest, staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StaffAttendanceResponse.toResponse(staffAttendance));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        StaffAttendance staffAttendance = attendanceRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        attendanceRepository.deleteById(integer);
        checkAndDeleteDailyWage(staffAttendance);
        return new ApiResponse(Constants.DELETED, true, staffAttendance);
    }

    private void dailyWageSetting(StaffAttendance staffAttendance) {
        Salary salary = salaryRepository.findByUserIdAndActiveTrue(staffAttendance.getUser().getId()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        double dailyWage = (salary.getFix() + salary.getClassLeaderSalary()) / salary.getUser().getWorkDays();
        dailyWage = Math.round(dailyWage * 100) / 100D;
        salary.setSalary(salary.getSalary() + dailyWage);
        salaryRepository.save(salary);
    }

    private void checkAndDeleteDailyWage(StaffAttendance staffAttendance) {
        if (staffAttendance.getDate().equals(LocalDate.now())) {
            Salary salary = salaryRepository.findByUserIdAndActiveTrue(staffAttendance.getUser().getId()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
            double dailyWage = (salary.getFix() + salary.getClassLeaderSalary()) / salary.getUser().getWorkDays();
            dailyWage = Math.round(dailyWage * 100) / 100D;
            if (salary.getSalary() > dailyWage) {
                salary.setSalary(salary.getSalary() - dailyWage);
            } else {
                salary.setAmountDebt(salary.getAmountDebt() + (dailyWage - salary.getSalary()));
                salary.setSalary(0);
            }
            salaryRepository.save(salary);
        }
    }

    private void setAndSave(StaffAttendanceRequest staffAttendanceRequest, StaffAttendance staffAttendance) {
        staffAttendance.setUser(userRepository.findById(staffAttendanceRequest.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        staffAttendance.setBranch(branchRepository.findById(staffAttendanceRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        attendanceRepository.save(staffAttendance);
    }
}