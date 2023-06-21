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

    public ApiResponse giveCashAdvance(Integer salaryId, double cashSalary) {
        Salary salary = checkById(salaryId);
        salary.setCashAdvance(salary.getCashAdvance() + cashSalary);
        salary.setGivenSalary(salary.getGivenSalary() + cashSalary);
        salary.setSalary(salary.getSalary() - cashSalary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse givePartlySalary(Integer salaryId, double partlySalary) {
        Salary salary = checkById(salaryId);
        salary.setPartlySalary(salary.getPartlySalary() + partlySalary);
        salary.setGivenSalary(salary.getGivenSalary() + partlySalary);
        salary.setSalary(salary.getSalary() - partlySalary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse getCurrentMonthFixSalary(String fromDate, String toDate, Integer id) {
        Salary salary = checkById(id);
        double monthlyAmount = getMonthlyAmount(fromDate, toDate, salary, salary.getUser());
        salary.setCurrentMonthSalary(monthlyAmount);
        setOylik(salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse getCurrentMonthTeachingHoursSalary(Integer salaryId) {
        Salary salary = checkById(salaryId);
        double overall = 0;
        List<TeachingHours> all = teachingHoursRepository.findAllByTeacherId(salary.getUser().getId());
        for (TeachingHours teachingHours : all) {
            overall += teachingHours.getTypeOfWork().getPrice() * teachingHours.getLessonHours();
        }
        salary.setCurrentMonthSalary(overall + salary.getClassLeaderSalary());
        setOylik(salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveSalary(Integer salaryId, double salaryAmount) {
        Salary salary = checkById(salaryId);
        if (salary.getSalary() == salaryAmount) {
            salary.setGivenSalary(salary.getGivenSalary() + salaryAmount);
            salary.setSalary(0);
            salary.setActive(false);
        } else {
            throw new RecordNotFoundException(Constants.SALARY_NOT_CORRECT);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse currentMonthSalaryAmount(Integer salaryId) {
        Salary salary = checkById(salaryId);
        double salaryAmountBeGiven = salary.getCurrentMonthSalary() - salary.getGivenSalary();
        salaryAmountBeGiven = Math.round((salaryAmountBeGiven * 100) / 100D);
        return new ApiResponse(Constants.SUCCESSFULLY, true, salaryAmountBeGiven);
    }

    //bu korxona o'tgan oy uchun pul bera olmay qolgandagi oyligi
    public ApiResponse giveRemainSalary(Integer salaryId, double salaryAmount) {
        Salary salary = checkById(salaryId);
        double remain = salary.getSalary() - salaryAmount;
        if (remain == 0) {
            double sal = Math.round((salary.getGivenSalary() + salaryAmount) * 100) / 100D;
            salary.setGivenSalary(sal);
            salary.setSalary(0);
            salary.setActive(false);
        } else if (remain < 0) {
            throw new RecordNotFoundException(Constants.EXTRA_MONEY_WAS_ADDED);
        } else {
            throw new RecordNotFoundException(Constants.SALARY_NOT_FOUND);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
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

    private static void setOylik(Salary salary) {
        double oylik = Math.round((salary.getCurrentMonthSalary() - salary.getGivenSalary()) * 100) / 100D;
        if (oylik >= 0) {
            salary.setSalary(oylik);
        } else {
            salary.setAmountDebt(oylik);
            salary.setActive(false);
        }
    }

    private double checkIsClassLeader(User user, double monthlyAmount) {
        if (user.getStudentClass() != null) {
            monthlyAmount += classLeaderSalary;
        }
        return monthlyAmount;
    }

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

    private User checkByUserId(SalaryRequest salaryRequest) {
        return userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
    }
}
