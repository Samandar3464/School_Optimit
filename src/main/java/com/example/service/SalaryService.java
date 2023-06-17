package com.example.service;

import com.example.entity.Salary;
import com.example.entity.StaffAttendance;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.model.response.SalaryResponse;
import com.example.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, Integer> {

    private final SalaryRepository salaryRepository;
    private final StaffAttendanceService staffAttendanceService;
    private final UserService userService;
    private final double workDays = 10;
    private final double classLeaderSalary=500_000;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        checkByUserId(salaryRequest);
        Salary salary = Salary.toSalary(salaryRequest);
        salary.setDate(getLocalDate(salaryRequest.getDate()));
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }


    public ApiResponse giveCashAdvance(SalaryRequest salaryRequest) {
        Salary salary = getSalaryByIdAndMonth(salaryRequest);
//        if (salary.getRemainingSalary() < salaryRequest.getCashAdvance()) {
//            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
//        }
        salary.setCashAdvance(salaryRequest.getCashAdvance() + salary.getCashAdvance());
        salary.setGivenSalary(salaryRequest.getCashAdvance() + salary.getGivenSalary());
//        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - salaryRequest.getCashAdvance()));
        if (salary.getRemainingSalary() == 0) {
            salary.setActive(false);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, salary.getRemainingSalary());
    }

    private Salary getSalaryByIdAndMonth(SalaryRequest salaryRequest) {
        return salaryRepository.findByIdAndMonthAndActiveTrue(salaryRequest.getId(), salaryRequest.getMonth()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    public ApiResponse givePartlySalary(SalaryRequest salaryRequest) {
        Salary salary = getSalaryByIdAndMonth(salaryRequest);
//        if (salary.getRemainingSalary() < salaryRequest.getPartlySalary()) {
//            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
//        }
        salary.setPartlySalary(salary.getPartlySalary() + salaryRequest.getPartlySalary());
//        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - salaryRequest.getPartlySalary()));
        salary.setGivenSalary(salary.getGivenSalary() + salaryRequest.getPartlySalary());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, salary.getPartlySalary());
    }

    public ApiResponse currentMonthSalary(String fromDate,String toDate,Integer id) {
        Salary salary = checkById(id);
        User user = userService.checkUserExistById(salary.getUserId());
        List<StaffAttendance> date = staffAttendanceService.findAllByUserAndDateBetween(userService.toLocalDate(fromDate), userService.toLocalDate(toDate),user);
        double daySalary = salary.getFix() / workDays;
        double monthSalary = date.size() * daySalary;
        if (user.getStudentClass() != null){
            monthSalary += classLeaderSalary;
        }
        salary.setCurrentMonthSalary(monthSalary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY,true,toResponse(salary));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(Constants.FOUND, true, toResponse(checkById(integer)));
    }

    @Override
    public ApiResponse update(SalaryRequest salaryRequest) {
        checkById(salaryRequest.getId());
        Salary salary = getSalary(salaryRequest);
        salary.setId(salaryRequest.getId());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toResponse(salary));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Salary salary = checkById(integer);
        salary.setActive(false);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, toResponse(salary));
    }

    private Salary checkById(Integer integer) {
        return salaryRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    private void checkByUserId(SalaryRequest salaryRequest) {
        userService.checkUserExistById(salaryRequest.getUserId());
    }

    private LocalDate getLocalDate(String date) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, dateTimeFormatter);
        } catch (Exception e) {
            throw new RecordNotFoundException(Constants.DATE_DO_NOT_MATCH + "  " + e);
        }
    }

    private Salary getSalary(SalaryRequest salaryRequest) {
        Salary salary = new Salary();
        salary.setFix(salaryRequest.getFix());
        salary.setActive(true);
        salary.setMonth(salaryRequest.getMonth());
        salary.setDate(getLocalDate(salaryRequest.getDate()));
        salary.setGivenSalary(salaryRequest.getGivenSalary());
        salary.setPartlySalary(salaryRequest.getPartlySalary());
        salary.setRemainingSalary(salaryRequest.getRemainingSalary());
        salary.setCurrentMonthSalary(salaryRequest.getCurrentMonthSalary());
        salary.setCashAdvance(salaryRequest.getCashAdvance());
        return salary;
    }

    private SalaryResponse toResponse(Salary salary) {
        return SalaryResponse
                .builder()
                .id(salary.getId())
                .month(salary.getMonth())
                .fix(salary.getFix())
                .cashAdvance(salary.getCashAdvance())
                .currentMonthSalary(salary.getCurrentMonthSalary())
                .givenSalary(salary.getGivenSalary())
                .partlySalary(salary.getPartlySalary())
                .remainingSalary(salary.getRemainingSalary())
                .build();
    }
}
