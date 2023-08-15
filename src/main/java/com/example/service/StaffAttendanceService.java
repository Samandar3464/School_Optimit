package com.example.service;

import com.example.entity.Branch;
import com.example.entity.Salary;
import com.example.entity.StaffAttendance;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StaffAttendanceRequest;
import com.example.model.response.StaffAttendanceResponse;
import com.example.model.response.StaffAttendanceResponsePage;
import com.example.repository.BranchRepository;
import com.example.repository.SalaryRepository;
import com.example.repository.StaffAttendanceRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAttendanceService implements BaseService<StaffAttendanceRequest, Integer> {

    private final StaffAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;


    @Override
    public ApiResponse create(StaffAttendanceRequest staffAttendanceRequest) {
        StaffAttendance staffAttendance = modelMapper.map(staffAttendanceRequest, StaffAttendance.class);
        if (attendanceRepository.findByUserIdAndDate(staffAttendanceRequest.getUserId(), LocalDate.now()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.STAFF_ATTENDANCE_ALREADY_EXISTS_FOR_THIS_DATE);
        }
        set(staffAttendanceRequest, staffAttendance);
        dailyWageSetting(staffAttendance);
        attendanceRepository.save(staffAttendance);
        StaffAttendanceResponse response = getStaffAttendanceResponse(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        StaffAttendance staffAttendance = attendanceRepository.findById(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        StaffAttendanceResponse staffAttendanceResponse = getStaffAttendanceResponse(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, staffAttendanceResponse);
    }

    public ApiResponse getAllByUserId(Integer id, int page, int size) {
        Page<StaffAttendance> all = attendanceRepository
                .findAllByUserId(id, PageRequest.of(page, size, Sort.Direction.DESC, "id"));

        StaffAttendanceResponsePage staffAttendanceResponsePage = getStaffAttendanceResponsePage(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, staffAttendanceResponsePage);
    }

    public ApiResponse getAllByBranchId(Integer id, int page, int size) {
        Page<StaffAttendance> all = attendanceRepository
                .findAllByBranchId(id, PageRequest.of(page, size, Sort.Direction.DESC, "id"));
        StaffAttendanceResponsePage staffAttendanceResponsePage = getStaffAttendanceResponsePage(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, staffAttendanceResponsePage);
    }

    @Override
    public ApiResponse update(StaffAttendanceRequest staffAttendanceRequest) {
        checkingUpdate(staffAttendanceRequest);
        StaffAttendance staffAttendance = modelMapper.map(staffAttendanceRequest, StaffAttendance.class);
        staffAttendance.setId(staffAttendanceRequest.getId());
        set(staffAttendanceRequest, staffAttendance);
        attendanceRepository.save(staffAttendance);
        StaffAttendanceResponse staffAttendanceResponse = getStaffAttendanceResponse(staffAttendance);
        return new ApiResponse(Constants.SUCCESSFULLY, true, staffAttendanceResponse);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        StaffAttendance staffAttendance = attendanceRepository
                .findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        attendanceRepository.deleteById(integer);
        checkAndDeleteDailyWage(staffAttendance);
        StaffAttendanceResponse staffAttendanceResponse = getStaffAttendanceResponse(staffAttendance);
        return new ApiResponse(Constants.DELETED, true, staffAttendanceResponse);
    }

    private void dailyWageSetting(StaffAttendance staffAttendance) {
        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(staffAttendance.getUser().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        double dailyWage = (salary.getFix() + salary.getClassLeaderSalary()) / salary.getUser().getWorkDays();
        dailyWage = Math.round(dailyWage * 100) / 100D;
        salary.setSalary(salary.getSalary() + dailyWage);
        salaryRepository.save(salary);
    }

    private void checkAndDeleteDailyWage(StaffAttendance staffAttendance) {
        if (staffAttendance.getDate().equals(LocalDate.now())) {
            Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(staffAttendance.getUser().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
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

    private void checkingUpdate(StaffAttendanceRequest staffAttendanceRequest) {
        attendanceRepository.findById(staffAttendanceRequest.getId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STAFF_ATTENDANCE_NOT_FOUND));
        if (attendanceRepository.findByUserIdAndDate(staffAttendanceRequest.getUserId(), LocalDate.now()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.STAFF_ATTENDANCE_ALREADY_EXISTS_FOR_THIS_DATE);
        }
    }

    private void set(StaffAttendanceRequest staffAttendanceRequest, StaffAttendance staffAttendance) {
        User user = userRepository.findById(staffAttendanceRequest.getUserId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
        Branch branch = branchRepository.findById(staffAttendanceRequest.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));

        staffAttendance.setDate(LocalDate.now());
        staffAttendance.setUser(user);
        staffAttendance.setBranch(branch);
    }

    private StaffAttendanceResponsePage getStaffAttendanceResponsePage(Page<StaffAttendance> all) {
        StaffAttendanceResponsePage staffAttendanceResponsePage = new StaffAttendanceResponsePage();
        List<StaffAttendanceResponse> staffAttendanceResponses = new ArrayList<>();
        all.forEach(staffAttendance -> {
            staffAttendanceResponses.add(getStaffAttendanceResponse(staffAttendance));
        });
        staffAttendanceResponsePage.setStaffAttendanceResponses(staffAttendanceResponses);
        staffAttendanceResponsePage.setTotalPage(all.getTotalPages());
        staffAttendanceResponsePage.setTotalElement(all.getTotalElements());
        return staffAttendanceResponsePage;
    }

    private StaffAttendanceResponse getStaffAttendanceResponse(StaffAttendance staffAttendance) {
        StaffAttendanceResponse response = modelMapper.map(staffAttendance, StaffAttendanceResponse.class);
        response.setDate(staffAttendance.getDate().toString());
        return response;
    }
}