package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.model.response.SalaryResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, Integer> {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final AdditionalExpenseRepository expenseRepository;
    private final StudentClassRepository studentClassRepository;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        if (salaryRepository.findByUserIdAndActiveTrue(salaryRequest.getUserId()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_EXISTS);
        }
        Salary salary = Salary.toSalary(salaryRequest);
        if (studentClassRepository.findByClassLeaderId(salaryRequest.getUserId()).isPresent()) {
            salary.setClassLeaderSalary(500000);
            salary.setSalary(salary.getSalary() + 500000);
        }
        setAndSave(salaryRequest, salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer userId) {
        SalaryResponse response = SalaryResponse.toResponse(findByUserIdAndActiveTrue(userId));
        return new ApiResponse(Constants.FOUND, true, response);
    }

    @Override
    public ApiResponse update(SalaryRequest salaryRequest) {
        Salary salary1 = findByUserIdAndActiveTrue(salaryRequest.getUserId());
        Salary salary = Salary.toSalary(salaryRequest);
        salary.setId(salary1.getId());
        setAndSave(salaryRequest, salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    @Override
    public ApiResponse delete(Integer userId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setActive(false);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, SalaryResponse.toResponse(salary));
    }


    public ApiResponse giveCashAdvance(Integer userId, double cashSalary, Integer paymentTypeId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setCashAdvance(salary.getCashAdvance() + cashSalary);
        setAndCheckAndSave(cashSalary, paymentTypeId, salary, "given cash salary to employee");
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse givePartlySalary(Integer userId, double partlySalary, Integer paymentTypeId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setPartlySalary(salary.getPartlySalary() + partlySalary);
        setAndCheckAndSave(partlySalary, paymentTypeId, salary, "given partly salary to employee");
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveSalary(Integer userId, boolean debtCollection, Integer paymentTypeId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        if (salary.getDate().getMonth() != LocalDate.now().getMonth()) {
            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_GIVEN_FOR_THIS_MONTH);
        }
        String message = debtCollection ? salary.getAmountDebt() + " qarz ushlab qolindi.  " : "";
        repaymentOfDebtIfAny(debtCollection, salary);

        double givingSalary = salary.getSalary();
        salary.setGivenSalary(salary.getGivenSalary() + givingSalary);
        salary.setSalary(0);
        salary.setActive(false);

        createNewSalary(salary);
        addExpense("given salary to employee", givingSalary, salary, getPaymentType(paymentTypeId));
        salaryRepository.save(salary);
        message += "bu oyligi :  " + givingSalary;
        return new ApiResponse(message, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveDebtToEmployee(Integer userId, double debtAmount, Integer paymentTypeId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        double money = salary.getSalary() - debtAmount;
        if (money >= 0) {
            salary.setSalary(salary.getClassLeaderSalary() + money);
        } else {
            salary.setSalary(0);
            salary.setAmountDebt(salary.getAmountDebt() + Math.abs(money));
        }
        salary.setGivenSalary(salary.getGivenSalary() + debtAmount);
        addExpense("given debit for employee ", debtAmount, salary, getPaymentType(paymentTypeId));
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse debtRepayment(Integer userId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        boolean isDebtAvailable = salary.getAmountDebt() > 0;
        repaymentOfDebtIfAny(true, salary);
        salaryRepository.save(salary);
        String message = !isDebtAvailable ? Constants.NO_DEBT_EXISTS
                : salary.getSalary() - salary.getAmountDebt() >= 0 ? Constants.DEBT_WAS_COLLECTED
                : Constants.SALARY_NOT_ENOUGH_REMAINING_DEBT + salary.getAmountDebt();
        return new ApiResponse(message, true, SalaryResponse.toResponse(salary));
    }

    private static void repaymentOfDebtIfAny(boolean debtCollection, Salary salary) {
        if (debtCollection) {
            double salaryWithoutDebit = salary.getSalary() - salary.getAmountDebt();
            if (salaryWithoutDebit >= 0) {
                salary.setSalary(salaryWithoutDebit);
                salary.setAmountDebt(0);
            } else {
                salary.setSalary(0);
                salary.setAmountDebt(Math.abs(salaryWithoutDebit));
            }
        }
    }

    private void createNewSalary(Salary salary) {
        Salary newSalary = new Salary();
        newSalary.setClassLeaderSalary(salary.getClassLeaderSalary());
        newSalary.setActive(true);
        newSalary.setDate(salary.getDate().plusMonths(1));
        newSalary.setFix(salary.getFix());
        newSalary.setBranch(salary.getBranch());
        newSalary.setUser(salary.getUser());
        newSalary.setSalary(newSalary.getFix() + newSalary.getClassLeaderSalary());
        newSalary.setAmountDebt(salary.getAmountDebt() > 0 ? salary.getAmountDebt() : 0);
        salaryRepository.save(newSalary);
    }

    private void setAndCheckAndSave(double money, Integer paymentTypeId, Salary salary, String message) {
        salary.setGivenSalary(salary.getGivenSalary() + money);
        if (salary.getSalary() >= money) {
            salary.setSalary(salary.getSalary() - money);
        } else {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
        addExpense(message, money, salary, getPaymentType(paymentTypeId));
        salaryRepository.save(salary);
    }

    private void setAndSave(SalaryRequest salaryRequest, Salary salary) {
        salary.setUser(userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        salary.setBranch(branchRepository.findById(salaryRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        salaryRepository.save(salary);
    }

    private void addExpense(String message, double cashSalary, Salary salary, PaymentType paymentType) {
        AdditionalExpense expense = new AdditionalExpense();
        expense.setSumma(cashSalary);
        expense.setReason(message);
        expense.setTaker(salary.getUser());
        expense.setBranch(salary.getBranch());
        expense.setCreatedTime(LocalDateTime.now());
        expense.setPaymentType(paymentType);
        expenseRepository.save(expense);
    }

    private PaymentType getPaymentType(Integer paymentTypeId) {
        return paymentTypeRepository.findById(paymentTypeId).orElseThrow(() -> new RecordNotFoundException(Constants.PAYMENT_TYPE_NOT_FOUND));
    }

    private Salary findByUserIdAndActiveTrue(Integer integer) {
        return salaryRepository.findByUserIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }
}
