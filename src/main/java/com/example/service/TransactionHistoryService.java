package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.MainBalanceResponse;
import com.example.model.response.StudentResponse;
import com.example.model.response.TransactionHistoryResponse;
import com.example.model.response.UserResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService implements BaseService<TransactionHistoryRequest, Integer> {

    private final MainBalanceRepository mainBalanceRepository;
    private final ModelMapper modelMapper;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class, RecordNotFoundException.class})
    public ApiResponse create(TransactionHistoryRequest request) {
        TransactionHistory transactionHistory = modelMapper.map(request, TransactionHistory.class);
        setTransactionHistory(request, transactionHistory);
        transactionWithBalance(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        TransactionHistoryResponse response = getTransactionHistoryResponse(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse getById(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        TransactionHistoryResponse response = getTransactionHistoryResponse(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse findAllByBranch_IdAndActiveTrue(Integer branchId) {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository
                .findAllByBranch_IdAndActiveTrue(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<TransactionHistoryResponse> transactionHistoryResponses = new ArrayList<>();
        transactionHistories.forEach(transactionHistory -> {
            transactionHistoryResponses.add(getTransactionHistoryResponse(transactionHistory));
        });
        return new ApiResponse(Constants.SUCCESSFULLY, true, transactionHistoryResponses);
    }

    public ApiResponse findAllByActiveTrue() {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByActiveTrue(Sort.by(Sort.Direction.DESC, "id"));
        List<TransactionHistoryResponse> transactionHistoryResponses = new ArrayList<>();
        transactionHistories.forEach(transactionHistory -> {
            transactionHistoryResponses.add(getTransactionHistoryResponse(transactionHistory));
        });
        return new ApiResponse(Constants.SUCCESSFULLY, true, transactionHistoryResponses);
    }

    @Override
    public ApiResponse update(TransactionHistoryRequest request) {
        TransactionHistory oldTransaction = getTransactionHistory(request.getId());
        TransactionHistory transactionHistory = modelMapper.map(request,TransactionHistory.class);

        transactionHistory.setId(request.getId());
        setTransactionHistory(request, transactionHistory);

        rollBackTransaction(oldTransaction);
        transactionWithBalance(transactionHistory);

        transactionHistoryRepository.save(transactionHistory);
        TransactionHistoryResponse response = getTransactionHistoryResponse(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        transactionHistory.setActive(false);
        rollBackTransaction(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        TransactionHistoryResponse response = getTransactionHistoryResponse(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public void transactionWithBalance(TransactionHistory transactionHistory) {

        MainBalance mainBalance = transactionHistory.getMainBalance();

        if (transactionHistory.getExpenseType().equals(ExpenseType.SALARY) || transactionHistory.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE) || transactionHistory.getExpenseType().equals(ExpenseType.STUDENT_EXPENSE)) {

            withdrawMoneyFromTheBalance(transactionHistory, mainBalance);

        } else {

            transferMoneyToTheBalance(transactionHistory, mainBalance);

        }

        mainBalanceRepository.save(mainBalance);
    }

    private void withdrawMoneyFromTheBalance(TransactionHistory transactionHistory, MainBalance mainBalance) {

        if (mainBalance.getBalance() >= transactionHistory.getMoneyAmount()) {

            if (transactionHistory.getPaymentType().equals(com.example.enums.PaymentType.CASH)) {

                if (mainBalance.getCashBalance() >= transactionHistory.getMoneyAmount()) {

                    mainBalance.setCashBalance(mainBalance.getCashBalance() - transactionHistory.getMoneyAmount());

                } else {

                    throw new RecordNotFoundException(Constants.CASH_BALANCE_NOT_ENOUGH_SUMMA);

                }

            } else {

                if (mainBalance.getPlasticBalance() >= transactionHistory.getMoneyAmount()) {

                    mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() - transactionHistory.getMoneyAmount());

                } else {

                    throw new RecordNotFoundException(Constants.PLASTIC_BALANCE_NOT_ENOUGH_SUMMA);

                }

            }

            mainBalance.setBalance(mainBalance.getBalance() - transactionHistory.getMoneyAmount());

        } else {

            throw new RecordNotFoundException(Constants.BALANCE_NOT_ENOUGH_SUMMA);

        }
    }

    private void transferMoneyToTheBalance(TransactionHistory transactionHistory, MainBalance mainBalance) {

        if (transactionHistory.getExpenseType().equals(ExpenseType.PAYMENT) || transactionHistory.getExpenseType().equals(ExpenseType.ADDITIONAL_PAYMENT) || transactionHistory.getExpenseType().equals(ExpenseType.STUDENT_PAYMENT)) {

            if (transactionHistory.getPaymentType().equals(com.example.enums.PaymentType.CASH)) {

                mainBalance.setCashBalance(mainBalance.getCashBalance() + transactionHistory.getMoneyAmount());

            } else {

                mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() + transactionHistory.getMoneyAmount());

            }

            mainBalance.setBalance(mainBalance.getBalance() + transactionHistory.getMoneyAmount());

        }
    }

    public void rollBackTransaction(TransactionHistory oldTransaction) {

        MainBalance mainBalance = oldTransaction.getMainBalance();

        if (oldTransaction.getExpenseType().equals(ExpenseType.SALARY) || oldTransaction.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE) || oldTransaction.getExpenseType().equals(ExpenseType.STUDENT_EXPENSE)) {

            rollBackMoneyToBalance(oldTransaction, mainBalance);

        } else {

            rollBackMoneyFromBalance(oldTransaction, mainBalance);

        }

        mainBalanceRepository.save(mainBalance);
    }

    private void rollBackMoneyFromBalance(TransactionHistory oldTransaction, MainBalance mainBalance) {

        if (oldTransaction.getExpenseType().equals(ExpenseType.PAYMENT) || oldTransaction.getExpenseType().equals(ExpenseType.ADDITIONAL_PAYMENT) || oldTransaction.getExpenseType().equals(ExpenseType.STUDENT_PAYMENT)) {

            if (oldTransaction.getPaymentType().equals(PaymentType.CASH)) {

                if (mainBalance.getCashBalance() >= oldTransaction.getMoneyAmount()) {

                    mainBalance.setCashBalance(mainBalance.getCashBalance() - oldTransaction.getMoneyAmount());

                } else {

                    throw new RecordNotFoundException(Constants.CASH_BALANCE_NOT_ENOUGH_SUMMA);

                }

            } else {

                if (mainBalance.getPlasticBalance() >= oldTransaction.getMoneyAmount()) {

                    mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() - oldTransaction.getMoneyAmount());

                } else {

                    throw new RecordNotFoundException(Constants.PLASTIC_BALANCE_NOT_ENOUGH_SUMMA);

                }

            }

            if (mainBalance.getBalance() >= oldTransaction.getMoneyAmount()) {

                mainBalance.setBalance(mainBalance.getBalance() - oldTransaction.getMoneyAmount());

            } else {

                throw new RecordNotFoundException(Constants.BALANCE_NOT_ENOUGH_SUMMA);

            }
        }
    }

    private void rollBackMoneyToBalance(TransactionHistory oldTransaction, MainBalance mainBalance) {

        if (oldTransaction.getPaymentType().equals(PaymentType.CASH)) {

            mainBalance.setCashBalance(mainBalance.getCashBalance() + oldTransaction.getMoneyAmount());

        } else {

            mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() + oldTransaction.getMoneyAmount());

        }

        mainBalance.setBalance(mainBalance.getBalance() + oldTransaction.getMoneyAmount());

    }

    private void setTransactionHistory(TransactionHistoryRequest request, TransactionHistory transactionHistory) {
        MainBalance mainBalance = mainBalanceRepository.findByIdAndActiveTrue(request.getMainBalanceId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND));
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        User user = request.getPhoneNumber() == null ? null
                : userRepository.findByPhoneNumberAndBlockedFalse(request.getPhoneNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND));
        Student student = request.getAccountNumber() == null ? null
                : studentRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND));

        transactionHistory.setActive(true);
        transactionHistory.setDate(LocalDateTime.now());
        transactionHistory.setMainBalance(mainBalance);
        transactionHistory.setBranch(branch);
        transactionHistory.setTaker(user);
        transactionHistory.setStudent(student);
    }

    private TransactionHistory getTransactionHistory(Integer integer) {
        return transactionHistoryRepository.findByIdAndActiveTrue(integer)
                .orElseThrow(() -> new RecordNotFoundException(Constants.TRANSACTION_HISTORY_NOT_FOUND));
    }

    private TransactionHistoryResponse getTransactionHistoryResponse(TransactionHistory transactionHistory) {
        TransactionHistoryResponse response = modelMapper.map(transactionHistory, TransactionHistoryResponse.class);

        StudentResponse studentResponse = transactionHistory.getStudent() == null ? null
                : modelMapper.map(transactionHistory.getStudent(), StudentResponse.class);
        UserResponse userResponse = transactionHistory.getTaker() == null ? null
                : modelMapper.map(transactionHistory.getTaker(), UserResponse.class);
        MainBalanceResponse mainBalanceResponse = modelMapper.map(transactionHistory.getMainBalance(), MainBalanceResponse.class);

        response.setDate(transactionHistory.getDate().toString());
        response.setStudent(studentResponse);
        response.setTaker(userResponse);
        response.setMainBalanceResponse(mainBalanceResponse);
        return response;
    }
}
