package com.example.service;

import com.example.entity.Salary;
import com.example.entity.TeachingHours;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.model.response.TeachingHoursResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachingHoursService implements BaseService<TeachingHoursRequest, Integer> {

    private final TeachingHoursRepository teachingHoursRepository;
    private final StudentClassRepository studentClassRepository;
    private final TypeOfWorkRepository typeOfWorkRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SalaryRepository salaryRepository;

    @Override
    public ApiResponse create(TeachingHoursRequest teachingHoursRequest) {
        TeachingHours teachingHours = TeachingHours.toTeachingHours(teachingHoursRequest);
        if (teachingHoursRepository.findByTeacherIdAndDateAndLessonHoursAndActiveTrue(teachingHoursRequest.getTeacherId(), teachingHoursRequest.getDate(), teachingHours.getLessonHours()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.TEACHING_HOURS_ALREADY_EXIST_THIS_DATE);
        }
        set(teachingHoursRequest, teachingHours);
        addPriceToSalary(teachingHours);
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }


    @Override
    public ApiResponse getById(Integer integer) {
        TeachingHours teachingHours = teachingHoursRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        return new ApiResponse(TeachingHoursResponse.teachingHoursDTO(teachingHours), true);
    }

    public ApiResponse getAll() {
        List<TeachingHoursResponse> all = TeachingHoursResponse.toAllResponse(teachingHoursRepository.findAll());
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    public ApiResponse getByTeacherIdAndActiveTrue(Integer id) {
        List<TeachingHoursResponse> all = TeachingHoursResponse.toAllResponse(teachingHoursRepository.findAllByTeacherIdAndActiveTrue(id, Sort.by(Sort.Direction.DESC,"id")));
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    public ApiResponse getByTeacherIdAndDate(Integer teacherId, LocalDate startDay, LocalDate finishDay) {
        List<TeachingHoursResponse> all = TeachingHoursResponse.toAllResponse(teachingHoursRepository.findAllByTeacherIdAndActiveTrueAndDateBetween(teacherId, startDay, finishDay, Sort.by(Sort.Direction.DESC,"id")));
        return new ApiResponse(Constants.SUCCESSFULLY, true, all);
    }

    @Override
    public ApiResponse update(TeachingHoursRequest teachingHoursRequest) {
        TeachingHours teachingHours = teachingHoursRepository.findByTeacherIdAndDateAndLessonHoursAndActiveTrue(teachingHoursRequest.getTeacherId(), teachingHoursRequest.getOldDate(), teachingHoursRequest.getOldLessonHours()).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        double oldPrice = teachingHours.getTypeOfWork().getPrice();

        teachingHours.setLessonHours(teachingHoursRequest.getLessonHours());
        teachingHours.setDate(teachingHoursRequest.getDate());
        set(teachingHoursRequest, teachingHours);
        teachingHoursRepository.save(teachingHours);

        double debtOrExtra = teachingHours.getTypeOfWork().getPrice() - oldPrice;
        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        hourlyWageSetting(debtOrExtra, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.teachingHoursDTO(teachingHours));
    }

    private static void hourlyWageSetting(double debtOrExtra, Salary salary) {
        if (debtOrExtra < 0) {
            debtOrExtra = Math.abs(debtOrExtra);
            if (salary.getSalary() > debtOrExtra) {
                salary.setSalary(salary.getSalary() - debtOrExtra);
            } else {
                salary.setAmountDebt(salary.getAmountDebt() + (debtOrExtra - salary.getSalary()));
                salary.setSalary(0);
            }
        } else {
            salary.setSalary(salary.getSalary() + debtOrExtra);
        }
    }

    private void addPriceToSalary(TeachingHours teachingHours) {
        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        if (salary.getAmountDebt() > 0) {
            if (salary.getSalary() > salary.getAmountDebt()) {
                salary.setSalary(salary.getSalary() - salary.getAmountDebt());
                salary.setAmountDebt(0);
            } else {
                salary.setAmountDebt(salary.getAmountDebt() - salary.getSalary());
                salary.setSalary(0);
            }
        } else {
            salary.setSalary(salary.getSalary() + teachingHours.getTypeOfWork().getPrice());
        }
        salaryRepository.save(salary);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TeachingHours teachingHours = teachingHoursRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        teachingHours.setActive(false);
        teachingHoursRepository.save(teachingHours);
        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
        hourlyWageSetting(-teachingHours.getTypeOfWork().getPrice(), salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, teachingHours);
    }

    private void set(TeachingHoursRequest teachingHoursRequest, TeachingHours teachingHours) {
        teachingHours.setStudentClass(studentClassRepository.findById(teachingHoursRequest.getStudentClassId()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_CLASS_NOT_FOUND)));
        teachingHours.setTeacher(userRepository.findById(teachingHoursRequest.getTeacherId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        teachingHours.setTypeOfWork(typeOfWorkRepository.findById(teachingHoursRequest.getTypeOfWorkId()).orElseThrow(() -> new RecordNotFoundException(Constants.TYPE_OF_WORK_NOT_FOUND)));
        teachingHours.setSubject(subjectRepository.findById(teachingHoursRequest.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
    }
}