package com.example.service;

import com.example.entity.Salary;
import com.example.entity.TeachingHours;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TeachingHoursRequest;
import com.example.model.response.TeachingHoursResponse;
import com.example.model.response.TeachingHoursResponseForPage;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        if (teachingHoursRepository.findByTeacherIdAndDateAndLessonHoursAndActiveTrue(teachingHoursRequest.getTeacherId(), teachingHoursRequest.getDate(), teachingHoursRequest.getLessonHours()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.TEACHING_HOURS_ALREADY_EXIST_THIS_DATE);
        }
        set(teachingHoursRequest, teachingHours);
        setSalary(teachingHours);
        teachingHoursRepository.save(teachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.toResponse(teachingHours));
    }


    @Override
    public ApiResponse getById(Integer integer) {
        TeachingHours teachingHours = teachingHoursRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.toResponse(teachingHours));
    }

    public ApiResponse getAll() {
        List<TeachingHoursResponse> response = TeachingHoursResponse.toAllResponse(teachingHoursRepository.findAllByActiveTrue());
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse getByTeacherIdAndActiveTrue(Integer id, int page, int size) {
        Page<TeachingHours> all = teachingHoursRepository.findAllByTeacherIdAndActiveTrue(id, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        List<TeachingHoursResponse> response = TeachingHoursResponse.toAllResponse(all.getContent());
        return new ApiResponse(Constants.SUCCESSFULLY, true, new TeachingHoursResponseForPage(response, all.getTotalElements(), all.getTotalPages(), all.getNumber()));
    }

    public ApiResponse getByTeacherIdAndDate(Integer teacherId, LocalDate startDay, LocalDate finishDay, int page, int size) {
        Page<TeachingHours> all = teachingHoursRepository.findAllByTeacherIdAndActiveTrueAndDateBetween(teacherId, startDay, finishDay, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        List<TeachingHoursResponse> response = TeachingHoursResponse.toAllResponse(all.getContent());
        return new ApiResponse(Constants.SUCCESSFULLY, true, new TeachingHoursResponseForPage(response, all.getTotalElements(), all.getTotalPages(), all.getNumber()));
    }

    @Override
    public ApiResponse update(TeachingHoursRequest teachingHoursRequest) {
        TeachingHours oldTransaction = teachingHoursRepository.findByIdAndActiveTrue(teachingHoursRequest.getId()).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        if (!oldTransaction.getDate().equals(LocalDate.now())) {
            throw new RecordNotFoundException(Constants.DO_NOT_CHANGE);
        }
        TeachingHours newTeachingHours = TeachingHours.toTeachingHours(teachingHoursRequest);
        newTeachingHours.setId(teachingHoursRequest.getId());
        set(teachingHoursRequest, newTeachingHours);
        hourlyWageSetting(newTeachingHours, oldTransaction.getTypeOfWork().getPrice());
        teachingHoursRepository.save(newTeachingHours);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TeachingHoursResponse.toResponse(newTeachingHours));
    }

    private void hourlyWageSetting(TeachingHours teachingHours, double oldMoney) {

        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));

        double debtOrExtra = teachingHours.getTypeOfWork().getPrice() - oldMoney;

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

        salaryRepository.save(salary);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TeachingHours teachingHours = teachingHoursRepository.findById(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TEACHING_HOURS_NOT_FOUND));
        if (teachingHours.getDate().equals(LocalDate.now())) {
            teachingHours.setActive(false);
            setForDelete(teachingHours);
            teachingHoursRepository.save(teachingHours);
        } else {
            throw new RecordNotFoundException(Constants.DO_NOT_CHANGE);
        }
        return new ApiResponse(Constants.DELETED, true, TeachingHoursResponse.toResponse(teachingHours));
    }

    private void setForDelete(TeachingHours teachingHours) {

        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));

        if (salary.getSalary() > teachingHours.getTypeOfWork().getPrice()) {

            salary.setSalary(salary.getSalary() - teachingHours.getTypeOfWork().getPrice());

        } else {

            salary.setAmountDebt(salary.getAmountDebt() + teachingHours.getTypeOfWork().getPrice());

            salary.setAmountDebt(salary.getAmountDebt() - salary.getSalary());

        }
        salaryRepository.save(salary);
    }

    private void setSalary(TeachingHours teachingHours) {

        Salary salary = salaryRepository.findByUserPhoneNumberAndActiveTrue(teachingHours.getTeacher().getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));

        salary.setSalary(salary.getSalary() + teachingHours.getTypeOfWork().getPrice());

        if (salary.getAmountDebt() > 0) {

            if (salary.getSalary() > salary.getAmountDebt()) {

                salary.setSalary(salary.getSalary() - salary.getAmountDebt());

                salary.setAmountDebt(0);

            } else {

                salary.setAmountDebt(salary.getAmountDebt() - salary.getSalary());

                salary.setSalary(0);

            }

        }

        salaryRepository.save(salary);

    }

    private void set(TeachingHoursRequest teachingHoursRequest, TeachingHours teachingHours) {
        teachingHours.setStudentClass(studentClassRepository.findById(teachingHoursRequest.getStudentClassId()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_CLASS_NOT_FOUND)));
        teachingHours.setTeacher(userRepository.findById(teachingHoursRequest.getTeacherId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        teachingHours.setTypeOfWork(typeOfWorkRepository.findById(teachingHoursRequest.getTypeOfWorkId()).orElseThrow(() -> new RecordNotFoundException(Constants.TYPE_OF_WORK_NOT_FOUND)));
        teachingHours.setSubject(subjectRepository.findById(teachingHoursRequest.getSubjectId()).orElseThrow(() -> new RecordNotFoundException(Constants.SUBJECT_NOT_FOUND)));
    }
}