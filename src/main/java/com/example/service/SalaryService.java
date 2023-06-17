package com.example.service;

import com.example.entity.Salary;
import com.example.enums.Constants;
import com.example.enums.Months;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.model.response.SalaryResponse;
import com.example.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, Integer> {

    private final SalaryRepository salaryRepository;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        Salary salary = new Salary();
        salary.setFix(salaryRequest.getFix());
        salary.setDate(getLocalDate(salaryRequest));
        salary.setActive(true);
        salary.setUserId(salaryRequest.getUserId());
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    public ApiResponse giveCashAdvance(Integer id, Months months, double cashAdvance) {
        Salary salary = salaryRepository.findByIdAndMonthAndActiveTrue(id, months);
        if (salary.getRemainingSalary() < cashAdvance) {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
        salary.setCashAdvance(cashAdvance + salary.getCashAdvance());
        salary.setGivenSalary(cashAdvance + salary.getGivenSalary());
        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - cashAdvance));
        if (salary.getRemainingSalary() == 0) {
            salary.setActive(false);
        }
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY,true, salary);
    }

    public ApiResponse givePartlySalary(Integer id, Months months, double partlySalary) {
        Salary salary = salaryRepository.findByIdAndMonthAndActiveTrue(id, months);
        if (salary.getRemainingSalary() < partlySalary) {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
        salary.setPartlySalary(partlySalary);
        salary.setRemainingSalary(Math.abs(salary.getRemainingSalary() - partlySalary));
        salary.setGivenSalary(partlySalary+salary.getPartlySalary());
        salaryRepository.save(salary);
        return new ApiResponse();
    }

    public ApiResponse currentMonthSalary() {

        return new ApiResponse();
    }

    @Override
    public ApiResponse getById(Integer integer) {
        return new ApiResponse(Constants.FOUND, true, checkById(integer));
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

    private static LocalDate getLocalDate(SalaryRequest salaryRequest) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(salaryRequest.getDate(), dateTimeFormatter);
    }

    private Salary getSalary(SalaryRequest salaryRequest) {
        Salary salary = new Salary();
        salary.setFix(salaryRequest.getFix());
        salary.setActive(true);
        salary.setMonth(salaryRequest.getMonth());
        salary.setDate(getLocalDate(salaryRequest));
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
