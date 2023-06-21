package com.example.service;

import com.example.entity.Salary;
import com.example.entity.StaffAttendance;
import com.example.entity.TeachingHours;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.exception.RecordNotFoundException;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryHoursRequest;
import com.example.model.request.SalaryRequest;
import com.example.model.response.SalaryResponse;
import com.example.repository.SalaryRepository;
import com.example.repository.TeachingHoursRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, Integer> {

    private final SalaryRepository salaryRepository;
    private final TeachingHoursRepository teachingHoursRepository;
    private final StaffAttendanceService staffAttendanceService;
    private final UserRepository userRepository;
    double workDays = 24;
    double classLeaderSalary = 500_000;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        User user = checkByUserId(salaryRequest);
        Salary salary = Salary.toSalaryCreate(salaryRequest);
        set(salaryRequest, user, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse giveCashAdvance(SalaryRequest salaryRequest) {
        Salary salary = checkById(salaryRequest.getId());
        salary.setCashAdvance(salary.getCashAdvance() + salaryRequest.getCashAdvance());
        salary.setGivenSalary(salary.getGivenSalary() + salaryRequest.getCashAdvance());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse givePartlySalary(SalaryRequest salaryRequest) {
        Salary salary = checkById(salaryRequest.getId());
        salary.setPartlySalary(salary.getPartlySalary() + salaryRequest.getPartlySalary());
        salary.setGivenSalary(salary.getGivenSalary() + salaryRequest.getPartlySalary());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse currentMonthSalary(String fromDate, String toDate, Integer id) {
        Salary salary = checkById(id);
        double monthlyAmount = getMonthlyAmount(fromDate, toDate, salary, salary.getUser());
        salary.setCurrentMonthSalary(monthlyAmount);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse getTeachingHoursSalary(SalaryHoursRequest salaryHoursRequest) {
        double overall = 0;
        Salary salary = checkById(salaryHoursRequest.getId());
        List<TeachingHours> all = teachingHoursRepository.findAllByTeacherId(salaryHoursRequest.getUserId());
        for (TeachingHours teachingHours : all) {
            overall += teachingHours.getTypeOfWork().getPrice() * teachingHours.getLessonHours();
        }
        salary.setCurrentMonthSalary(salary.getCurrentMonthSalary() + overall);
        double oylik = overall - salary.getGivenSalary();
        salary.setSalary(oylik);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveSalary(SalaryRequest salaryRequest) {
        Salary salary = checkById(salaryRequest.getId());
        double qolgan = salary.getCurrentMonthSalary() - salary.getGivenSalary();
        qolgan = Math.round(qolgan * 100) / 100D;
        set(salary, qolgan);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, qolgan);
    }


    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(Constants.FOUND, true, SalaryResponse.toResponse(checkById(integer)));
    }

    @Override
    public ApiResponse update(SalaryRequest salaryRequest) {
        checkById(salaryRequest.getId());
        Salary salary = Salary.toSalary(salaryRequest);
        salary.setUser(userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        salary.setDate(toLocalDate(salaryRequest.getDate()));
        salary.setId(salaryRequest.getId());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        Salary salary = checkById(integer);
        salary.setActive(false);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, SalaryResponse.toResponse(salary));
    }

    private double getMonthlyAmount(String fromDate, String toDate, Salary salary, User user) {
        List<StaffAttendance> workingDays = staffAttendanceService.findAllByUserAndDateBetween(toLocalDate(fromDate), toLocalDate(toDate), user);
        double dailyAmount = salary.getFix() / workDays;
        double monthlyAmount = workingDays.size() * dailyAmount;
        monthlyAmount = checkIsClassLeader(user, monthlyAmount);
        return Math.round(monthlyAmount * 100) / 100D;
    }

    private void set(SalaryRequest salaryRequest, User user, Salary salary) {
        salary.setUser(user);
        salary.setDate(toLocalDate(salaryRequest.getDate()));
        if (user.getStudentClass() != null) {
            salary.setClassLeaderSalary(classLeaderSalary);
        }
    }

    private void set(Salary salary, double qolgan) {
        if (qolgan >= 0) {
            salary.setGivenSalary(salary.getGivenSalary() + qolgan);
        } else {
            salary.setAmountDebt(Math.abs(qolgan));
        }
    }

    private double checkIsClassLeader(User user, double monthlyAmount) {
        if (user.getStudentClass() != null) {
            monthlyAmount += classLeaderSalary;
        }
        return monthlyAmount;
    }

//    private Salary getSalaryIfValidForPartly(SalaryRequest salaryRequest) {
//        Salary salary = getSalaryByIdAndMonth(salaryRequest);
//        if (salary.getRemainingSalary() < salaryRequest.getPartlySalary()) {
//            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
//        }
//        return salary;
//    }
//
//    private Salary getSalaryIfValidForCashAdvance(SalaryRequest salaryRequest) {
//        Salary salary = getSalaryByIdAndMonth(salaryRequest);
//        if (salary.getRemainingSalary() < salaryRequest.getCashAdvance()) {
//            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
//        }
//        return salary;
//    }

    private LocalDate toLocalDate(String date) {
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, dateTimeFormatter);
        } catch (Exception e) {
            throw new RecordNotFoundException(Constants.DATE_DO_NOT_MATCH + "  " + e);
        }
    }

    private Salary checkById(Integer integer) {
        return salaryRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    private Salary getSalaryByIdAndMonth(SalaryRequest salaryRequest) {
        return salaryRepository.findByIdAndMonthAndActiveTrue(salaryRequest.getId(), salaryRequest.getMonth()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    private User checkByUserId(SalaryRequest salaryRequest) {
        return userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
    }
}
