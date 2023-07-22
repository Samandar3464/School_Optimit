package com.example.service;

import com.example.entity.*;
import com.example.enums.ExpenseType;
import com.example.exception.UserNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.ExpenseDto;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.enums.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final TeachingHoursRepository teachingHoursRepository;
    private final StaffAttendanceRepository staffAttendanceRepository;
    private final UserRepository userRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final AdditionalExpenseRepository expenseRepository;
    private final LessonScheduleRepository lessonScheduleRepository;
    private final StudentClassRepository studentClassRepository;

    public ApiResponse calculateUserSalary(ExpenseDto dto) {
        Double salary = 0D;
        Double currentSumma = 0D;
        Double givenSalary = 0D;
        int staffUser = 0;
        User user = userRepository.findById(dto.getId()).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        List<LessonSchedule> lessonSchedules = lessonScheduleRepository.findAllByTeacherIdAndActiveTrueAndStartTimeBetween(user.getId(), dto.getStartDate(), dto.getEndDate());
        for (LessonSchedule lessonSchedule : lessonSchedules) {
            salary += lessonSchedule.getTypeOfWork().getPriceForPerHour();
        }

        Optional<StudentClass> studentClass = studentClassRepository.findByClassLeaderIdAndActiveTrue(user.getId());
        if (studentClass.isPresent()) {
            salary += studentClass.get().getSalaryForClassLeader();
        }
        Salary salaryNew = null;

//        List<TeachingHours> teachingHours = teachingHoursRepository.findAllByTeacherIdAndActiveTrueAndPassedDateBetween(user.getId(), dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate());
//        for (TeachingHours teachingHour : teachingHours) {
//            currentSumma += teachingHour.getTypeOfWork().getPriceForPerHour();
//        }
        currentSumma += studentClass.get().getSalaryForClassLeader();


        List<AdditionalExpense> additionalExpenses = expenseRepository.findAllByTakerIdAndExpenseTypeAndGivenDateBetween(user.getId(), ExpenseType.SALARY, dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate());
        for (AdditionalExpense additionalExpens : additionalExpenses) {
            givenSalary += additionalExpens.getSumma();
        }

        List<StaffAttendance> staffAttendances = staffAttendanceRepository.findAllByUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(user.getId(), dto.getStartDate(), dto.getEndDate());
        staffUser = staffAttendances.size();
        Optional<Salary> salaryNewOptional = salaryRepository.findByUserIdAndMonthsAndYearAndActiveTrue(user.getId() ,dto.getStartDate().getMonth(),dto.getStartDate().getYear());
        if (salaryNewOptional.isPresent()) {
            salaryNew = salaryNewOptional.get();
            salaryNew.setCurrentMonthSalary(salary);
            salaryNew.setGivenSalary(givenSalary);
            salaryNew.setCurrentSumma(currentSumma);
            salaryNew.setStaffUser(staffUser);
        } else {
            salaryNew = Salary.builder()
                    .currentSumma(currentSumma)
                    .currentMonthSalary(salary)
                    .givenSalary(givenSalary)
                    .user(user)
//                    .salaryDate(dto.getStartDate().toLocalDate())
                    .active(true)
                    .months(dto.getStartDate().getMonth())
                    .year(dto.getStartDate().getYear())
                    .staffUser(staffUser)
                    .build();
        }
        Salary save = salaryRepository.save(salaryNew);
        return new ApiResponse(save, true);
    }

}
