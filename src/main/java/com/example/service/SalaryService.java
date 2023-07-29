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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SalaryService implements BaseService<SalaryRequest, Integer> {

    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final StudentClassRepository studentClassRepository;
    private final TransactionHistoryService transactionHistoryService;

    @Override
    public ApiResponse create(SalaryRequest salaryRequest) {
        if (salaryRepository.findByUserIdAndActiveTrue(salaryRequest.getUserId()).isPresent()) {
            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_EXISTS);
        }
        Salary salary = Salary.toSalary(salaryRequest);
        setClassLeaderSalary(salaryRequest, salary);
        setAndSave(salaryRequest, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true);
    }

    @Override
    public ApiResponse getById(Integer userId) {
        SalaryResponse response = SalaryResponse.toResponse(findByUserIdAndActiveTrue(userId));
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse update(SalaryRequest salaryRequest) {
        findByUserIdAndActiveTrue(salaryRequest.getUserId());
        Salary salary = Salary.toSalary(salaryRequest);
        salary.setId(salaryRequest.getId());
        setAndSave(salaryRequest, salary);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    @Override
    public ApiResponse delete(Integer userId) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setActive(false);
        salaryRepository.save(salary);
        return new ApiResponse(Constants.DELETED, true, SalaryResponse.toResponse(salary));
    }


    public ApiResponse giveCashAdvance(Integer userId, double cashSalary, PaymentType paymentType) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setCashAdvance(salary.getCashAdvance() + cashSalary);
        setMoney(cashSalary, salary);
        salaryRepository.save(salary);
        transactionHistoryService.create(getTransactionHistoryRequest(userId, cashSalary, paymentType, salary,"Hodim naqd shaklida pul oldi"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private TransactionHistoryRequest getTransactionHistoryRequest(Integer userId, double money, PaymentType paymentType, Salary salary, String message) {
        return TransactionHistoryRequest
                        .builder()
                        .moneyAmount(money)
                        .date(LocalDateTime.now())
                        .paymentType(paymentType)
                        .takerId(userId)
                        .branchId(salary.getBranch().getId())
                        .expenseType(ExpenseType.ADDITIONAL_PAYMENT)
                        .comment(message)
                        .mainBalanceId(salary.getBranch().getMainBalance().getId())
                        .build();
    }

    public ApiResponse givePartlySalary(Integer userId, double partlySalary, PaymentType paymentType) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        salary.setPartlySalary(salary.getPartlySalary() + partlySalary);
        setMoney(partlySalary, salary);
        salaryRepository.save(salary);
        transactionHistoryService.create(getTransactionHistoryRequest(userId, partlySalary, paymentType, salary,"Hodimga qisman oylik berildi"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    public ApiResponse giveSalary(Integer userId, boolean debtCollection, PaymentType paymentType) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        if (salary.getDate().getMonth() != LocalDate.now().getMonth()) {
            throw new RecordAlreadyExistException(Constants.SALARY_ALREADY_GIVEN_FOR_THIS_MONTH);
        }
        repaymentOfDebtIfAny(debtCollection, salary);
        double givingSalary = getSalary(salary);

        createNewSalary(salary);
        salaryRepository.save(salary);
        transactionHistoryService.create(getTransactionHistoryRequest(userId, givingSalary, paymentType, salary,"Hodimga oylik berildi"));
        String message = getMessage(debtCollection, salary, givingSalary);
        return new ApiResponse(message, true, SalaryResponse.toResponse(salary));
    }

    private  double getSalary(Salary salary) {
        double givingSalary = salary.getSalary();
        salary.setGivenSalary(salary.getGivenSalary() + givingSalary);
        salary.setSalary(0);
        salary.setActive(false);
        return givingSalary;
    }

    private static String getMessage(boolean debtCollection, Salary salary, double givingSalary) {
        String message = debtCollection ? salary.getAmountDebt() + " qarz ushlab qolindi.  " : "";
        message += "bu oyligi :  " + givingSalary;
        return message;
    }

    public ApiResponse giveDebtToEmployee(Integer userId, double debtAmount, PaymentType paymentType) {
        Salary salary = findByUserIdAndActiveTrue(userId);
        setDebt(debtAmount, salary);
        salary.setGivenSalary(salary.getGivenSalary() + debtAmount);
        salaryRepository.save(salary);
        transactionHistoryService.create(getTransactionHistoryRequest(userId, debtAmount, paymentType, salary,"Hodim qarz oldi"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, SalaryResponse.toResponse(salary));
    }

    private static void setDebt(double debtAmount, Salary salary) {
        double money = salary.getSalary() - debtAmount;
        if (money >= 0) {
            salary.setSalary(salary.getClassLeaderSalary() + money);
        } else {
            salary.setSalary(0);
            salary.setAmountDebt(salary.getAmountDebt() + Math.abs(money));
        }
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

    private void setMoney(double money, Salary salary) {
        salary.setGivenSalary(salary.getGivenSalary() + money);
        if (salary.getSalary() >= money) {
            salary.setSalary(salary.getSalary() - money);
        } else {
            throw new RecordNotFoundException(Constants.SALARY_NOT_ENOUGH);
        }
    }

    private void setClassLeaderSalary(SalaryRequest salaryRequest, Salary salary) {
        if (studentClassRepository.findByClassLeaderIdAndActiveTrue(salaryRequest.getUserId()).isPresent()) {
            salary.setClassLeaderSalary(500000);
            salary.setSalary(salary.getSalary() + 500000);
        }
    }

    private void setAndSave(SalaryRequest salaryRequest, Salary salary) {
        salary.setUser(userRepository.findById(salaryRequest.getUserId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        salary.setBranch(branchRepository.findById(salaryRequest.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
    }

    private Salary findByUserIdAndActiveTrue(Integer integer) {
        return salaryRepository.findByUserIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.SALARY_NOT_FOUND));
    }
}
