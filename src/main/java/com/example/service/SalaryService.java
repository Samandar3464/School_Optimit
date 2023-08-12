package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.SalaryRequest;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.SalaryResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, String> {
    private final SalaryRepository salaryRepository;

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final MainBalanceRepository mainBalanceRepository;
    private final StudentClassRepository studentClassRepository;
    private final TransactionHistoryService transactionHistoryService;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        if (salaryRepository.findByUserPhoneNumberAndActiveTrue(salaryRequest.getPhoneNumber()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_EXISTS);
        }
        Salary salary = Salary.toCreate(salaryRequest);
        setSalaryForCreate(salaryRequest, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private void setSalaryForCreate(SalaryRequest salaryRequest, Salary salary) {
        if (studentClassRepository.findByClassLeaderPhoneNumberAndActiveTrue(salaryRequest.getPhoneNumber()).isPresent()) {
            salary.setClassLeaderSalary(salaryRequest.getClassLeaderSalary());
            salary.setSalary(salary.getSalary() + salary.getClassLeaderSalary());
        }
        setSalary(salaryRequest, salary);
    }

    @Override
    public ApiResponse getById(String phoneNumber) {
        SalaryResponse response = SalaryResponse.toResponse(findByUserPhoneNumberAndActiveTrue(phoneNumber));
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse update(SalaryRequest salaryRequest) {
        Salary salary1 = findByUserPhoneNumberAndActiveTrue(salaryRequest.getPhoneNumber());
        Salary salary = Salary.toSalary(salaryRequest);
        if (salary1.getDate().getDayOfMonth() != salary.getDate().getDayOfMonth()) {
            throw new RecordNotFoundException(Constants.DO_NOT_CHANGE);
        }
        salary.setId(salaryRequest.getId());
        setSalary(salaryRequest, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }


    @Override
    public ApiResponse delete(String phoneNumber) {
        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);
        salary.setActive(false);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveCashAdvance(String phoneNumber, double cashSalary, PaymentType paymentType) {

        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);

        setSalaryForGiveCashAdvanceMethod(cashSalary, salary);

        ApiResponse response = transactionForGiveCashAdvanceMethod(phoneNumber, cashSalary, paymentType, salary);

        if (response != null) return response;

        salaryRepository.save(salary);

        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private ApiResponse transactionForGiveCashAdvanceMethod(String phoneNumber, double cashSalary, PaymentType paymentType, Salary salary) {
        if (salary.getSalary() >= cashSalary) {
            try {
                transactionHistoryService.create(TransactionHistoryRequest.toTransactionHistoryRequest(phoneNumber, cashSalary, paymentType, ExpenseType.ADDITIONAL_EXPENSE, salary, "Hodim naqd shaklida pul oldi"));
            } catch (Exception e) {
                return new ApiResponse(e.getMessage(), false);
            }
        }
        return null;
    }

    private void setSalaryForGiveCashAdvanceMethod(double cashSalary, Salary salary) {

        salary.setCashAdvance(salary.getCashAdvance() + cashSalary);

        salary.setGivenSalary(salary.getGivenSalary() + cashSalary);

        if (salary.getSalary() >= cashSalary) {

            salary.setSalary(salary.getSalary() - cashSalary);

        } else {

            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);

        }
    }

    @Transactional(rollbackFor = {RecordNotFoundException.class, RecordNotFoundException.class})
    public ApiResponse givePartlySalary(String phoneNumber, double partlySalary, PaymentType paymentType) {

        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);

        setSalaryForGivePartlySalaryMethod(partlySalary, salary);

        ApiResponse response = transactionForGivePartlySalaryMethod(phoneNumber, partlySalary, paymentType, salary);

        if (response != null) return response;

        salaryRepository.save(salary);

        createNewSalary(salary);

        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private ApiResponse transactionForGivePartlySalaryMethod(String phoneNumber, double partlySalary, PaymentType paymentType, Salary salary) {
        if (salary.getSalary() >= partlySalary) {
            try {
                transactionHistoryService.create(TransactionHistoryRequest.toTransactionHistoryRequest(phoneNumber, partlySalary, paymentType, ExpenseType.ADDITIONAL_EXPENSE, salary, "Hodimga qisman oylik berildi"));
            } catch (Exception e) {
                return new ApiResponse(e.getMessage(), false);
            }
        }
        return null;
    }

    private void setSalaryForGivePartlySalaryMethod(double partlySalary, Salary salary) {

        salary.setPartlySalary(salary.getPartlySalary() + partlySalary);

        salary.setGivenSalary(salary.getGivenSalary() + partlySalary);

        if (salary.getSalary() >= partlySalary) {

            salary.setSalary(salary.getSalary() - partlySalary);

        } else {

            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);

        }

        salary.setActive(false);
    }

    public ApiResponse giveSalary(String phoneNumber, boolean debtCollection, PaymentType paymentType) {

        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);

        if (salary.getDate().getMonth() != LocalDate.now().getMonth()) {

            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_GIVEN_FOR_THIS_MONTH);

        }
        double salaryWithoutDebt = salary.getSalary() - salary.getAmountDebt();

        if (salaryWithoutDebt > 0) {

            ApiResponse response = getTransactionResponse(phoneNumber, salaryWithoutDebt, paymentType, salary, "Hodimga oylik berildi");

            if (response != null) return response;

        }

        repaymentOfDebtIfAny(debtCollection, salary);

        setSalaryForGiveSalaryMethod(salary);

        createNewSalary(salary);

        salaryRepository.save(salary);

        String message = getMessage(debtCollection, salary, salaryWithoutDebt > 0 ? salaryWithoutDebt : 0);

        return new ApiResponse(message, true, SalaryResponse.toResponse(salary));
    }

    private void setSalaryForGiveSalaryMethod(Salary salary) {
        salary.setGivenSalary(salary.getGivenSalary() + salary.getSalary());
        salary.setSalary(0);
        salary.setActive(false);
    }

    public ApiResponse giveDebtToEmployee(String phoneNumber, double debtAmount, PaymentType paymentType) {
        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);
        ApiResponse response = getTransactionResponse(phoneNumber, debtAmount, paymentType, salary, "Hodim pul oldi");
        if (response != null) return response;
        setSalaryForGiveDebtToEmployeeMethod(debtAmount, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private void setSalaryForGiveDebtToEmployeeMethod(double debtAmount, Salary salary) {

        double money = salary.getSalary() - debtAmount;

        if (money >= 0) {

            salary.setSalary(money);

        } else {

            salary.setSalary(0);

            salary.setAmountDebt(salary.getAmountDebt() + Math.abs(money));

        }

        salary.setGivenSalary(salary.getGivenSalary() + debtAmount);

    }

    public ApiResponse debtRepayment(String phoneNumber) {
        Salary salary = findByUserPhoneNumberAndActiveTrue(phoneNumber);
        boolean isDebtAvailable = salary.getAmountDebt() > 0;
        repaymentOfDebtIfAny(true, salary);
        salaryRepository.save(salary);
        String message = !isDebtAvailable ? Constants.NO_DEBT_EXISTS
                : salary.getSalary() - salary.getAmountDebt() >= 0 ? Constants.DEBT_WAS_COLLECTED
                : Constants.SALARY_NOT_ENOUGH_REMAINING_DEBT + salary.getAmountDebt();
        return new ApiResponse(message, true, SalaryResponse.toResponse(salary));
    }

    private ApiResponse getTransactionResponse(String phoneNumber, double cashSalary, PaymentType paymentType, Salary salary, String message) {
        try {
            transactionHistoryService.create(TransactionHistoryRequest.toTransactionHistoryRequest(phoneNumber, cashSalary, paymentType, ExpenseType.ADDITIONAL_EXPENSE, salary, message));
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
        return null;
    }

    private String getMessage(boolean debtCollection, Salary salary, double givingSalary) {
        String message = debtCollection ? salary.getAmountDebt() + " qarz ushlab qolindi.  " : "";
        message += "bu oyligi :  " + givingSalary;
        return message;
    }

    private void createNewSalary(Salary salary) {
        Salary newSalary = new Salary();
        newSalary.setClassLeaderSalary(salary.getClassLeaderSalary());
        newSalary.setActive(true);
        newSalary.setDate(salary.getDate().plusMonths(1));
        newSalary.setFix(salary.getFix());
        newSalary.setBranch(salary.getBranch());
        newSalary.setUser(salary.getUser());
        newSalary.setSalary(salary.getSalary());
        newSalary.setAmountDebt(salary.getAmountDebt() > 0 ? salary.getAmountDebt() : 0);
        salaryRepository.save(newSalary);
    }

    private void repaymentOfDebtIfAny(boolean debtCollection, Salary salary) {

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

    private void setSalary(SalaryRequest salaryRequest, Salary salary) {
        salary.setUser(userRepository.findByPhoneNumberAndBlockedFalse(salaryRequest.getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        salary.setMainBalance(mainBalanceRepository.findByIdAndActiveTrue(salaryRequest.getMainBalanceId()).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND)));
        salary.setBranch(branchRepository.findByIdAndDeleteFalse(salaryRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
    }

    private Salary findByUserPhoneNumberAndActiveTrue(String phoneNumber) {
        return salaryRepository.findByUserPhoneNumberAndActiveTrue(phoneNumber).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }

    public ApiResponse getAllByBranchId(Integer branchId) {
        List<SalaryResponse> salaryResponses = new ArrayList<>();
        List<Salary> all = salaryRepository.findAllByBranch_IdAndActiveTrue(branchId);
        all.forEach(salary -> {
            salaryResponses.add(SalaryResponse.toResponse(salary));
        });
        return new ApiResponse(Constants.SUCCESSFULLY, true, salaryResponses);
    }
}
