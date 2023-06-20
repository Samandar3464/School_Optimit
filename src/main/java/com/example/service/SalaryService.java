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
    private final double workDays = 24;
    private final double classLeaderSalary = 500_000;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        User user = checkByUserId(salaryRequest);
        Salary salary = Salary.toSalary(salaryRequest);
        salary.setUser(user);
        if (user.getStudentClass() != null) {
            salary.setClassLeaderSalary(classLeaderSalary);
            salary.setRemainingSalary(salary.getRemainingSalary() + classLeaderSalary);
        }
        salary.setDate(getLocalDate(salaryRequest.getDate()));
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse giveCashAdvance(SalaryRequest salaryRequest) {
        Salary salary = getSalaryIfValidForCashAdvance(salaryRequest);
        salary.setCashAdvance(salaryRequest.getCashAdvance() + salary.getCashAdvance());
        salary.setGivenSalary(salaryRequest.getCashAdvance() + salary.getGivenSalary());
        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - salaryRequest.getCashAdvance()));
        if (salary.getRemainingSalary() == 0) {
            salary.setActive(false);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toResponse(salary));
    }

    private Salary getSalaryIfValidForCashAdvance(SalaryRequest salaryRequest) {
        Salary salary = getSalaryByIdAndMonth(salaryRequest);
        if (salary.getRemainingSalary() < salaryRequest.getCashAdvance()) {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
        return salary;
    }

    public ApiResponse givePartlySalary(SalaryRequest salaryRequest) {
        Salary salary = getSalaryIfValidForPartly(salaryRequest);
        salary.setPartlySalary(salary.getPartlySalary() + salaryRequest.getPartlySalary());
        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - salaryRequest.getPartlySalary()));
        salary.setGivenSalary(salary.getGivenSalary() + salaryRequest.getPartlySalary());
        if (salary.getRemainingSalary() == 0) {
            salary.setActive(false);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toResponse(salary));
    }

    private Salary getSalaryIfValidForPartly(SalaryRequest salaryRequest) {
        Salary salary = getSalaryByIdAndMonth(salaryRequest);
        if (salary.getRemainingSalary() < salaryRequest.getPartlySalary()) {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
        return salary;
    }

    public ApiResponse currentMonthSalary(String fromDate, String toDate, Integer id) {
        Salary salary = checkById(id);
        double monthlyAmount = getMonthlyAmount(fromDate, toDate, salary, salary.getUser());
        salary.setCurrentMonthSalary(monthlyAmount);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, toResponse(salary));
    }

    public ApiResponse getTeachingHoursSalary(SalaryHoursRequest salaryHoursRequest) {
        
        double overall = 0;
        Salary salary = checkById(salaryHoursRequest.getId());
        List<TeachingHours> all = teachingHoursRepository.findAllByTeacherId(salaryHoursRequest.getUserId());
        for (TeachingHours teachingHours : all) {
            overall += teachingHours.getTypeOfWork().getPrice() * teachingHours.getLessonHours();
        }
        salary.setCurrentMonthSalary(overall);
        double oylik = overall - salary.getGivenSalary();
        
        return new ApiResponse(Constants.SUCCESSFULLY, true, toResponse(salary));
    }

    public ApiResponse giveSalary(SalaryRequest salaryRequest) {
        Salary salary = checkById(salaryRequest.getId());
        double qolgan = salary.getCurrentMonthSalary() - salary.getGivenSalary();
        qolgan = Math.round(qolgan * 100) / 100D;
        if (qolgan >= 0) {
            salary.setGivenSalary(salary.getGivenSalary() + qolgan);
            salary.setRemainingSalary(0);
        } else {
            salary.setAmountDebt(Math.abs(qolgan));
            salary.setRemainingSalary(0);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, qolgan);
    }

    private double getMonthlyAmount(String fromDate, String toDate, Salary salary, User user) {
        List<StaffAttendance> workingDays = staffAttendanceService.findAllByUserAndDateBetween(toLocalDate(fromDate), toLocalDate(toDate), user);
        double dailyAmount = salary.getFix() / workDays;
        double monthlyAmount = workingDays.size() * dailyAmount;
        if (user.getStudentClass() != null) {
            monthlyAmount += classLeaderSalary;
        }
        return Math.round(monthlyAmount * 100) / 100D;
    }

    private LocalDate toLocalDate(String toDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(toDate, dateTimeFormatter);
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

    private User checkByUserId(SalaryRequest salaryRequest) {
        return userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));
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
        salary.setUser(userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND)));
        salary.setMonth(salaryRequest.getMonth());
        salary.setDate(getLocalDate(salaryRequest.getDate()));
        salary.setGivenSalary(salaryRequest.getGivenSalary());
        salary.setPartlySalary(salaryRequest.getPartlySalary());
        salary.setRemainingSalary(salaryRequest.getRemainingSalary());
        salary.setCurrentMonthSalary(salaryRequest.getCurrentMonthSalary());
        salary.setCashAdvance(salaryRequest.getCashAdvance());
        return salary;
    }

    private Salary getSalaryByIdAndMonth(SalaryRequest salaryRequest) {
        return salaryRepository.findByIdAndMonthAndActiveTrue(salaryRequest.getId(), salaryRequest.getMonth()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    private SalaryResponse toResponse(Salary salary) {
        return SalaryResponse
                .builder()
                .id(salary.getId())
                .month(salary.getMonth())
                .fix(salary.getFix())
                .cashAdvance(salary.getCashAdvance())
                .currentMonthSalary(salary.getCurrentMonthSalary())
                .classLeaderSalary(salary.getClassLeaderSalary())
                .amountDebt(salary.getAmountDebt())
                .givenSalary(salary.getGivenSalary())
                .partlySalary(salary.getPartlySalary())
                .remainingSalary(salary.getRemainingSalary())
                .build();
    }
}
