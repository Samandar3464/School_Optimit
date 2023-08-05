package com.example.service;

import com.example.entity.MainBalance;
import com.example.entity.Student;
import com.example.entity.TransactionHistory;
import com.example.entity.User;
import com.example.enums.Constants;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.TransactionHistoryResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService implements BaseService<TransactionHistoryRequest, Integer> {

    private final MainBalanceRepository mainBalanceRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public ApiResponse create(TransactionHistoryRequest request) {
        TransactionHistory transactionHistory = TransactionHistory.toEntity(request);
        setTransactionHistory(request, transactionHistory);
        transactionWithBalance(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
    }

    public ApiResponse findAllByBranch_IdAndActiveTrue(Integer branchId) {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByBranch_IdAndActiveTrue(branchId, Sort.by(Sort.Direction.DESC, "id"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toAllResponse(transactionHistories));
    }

    public ApiResponse findAllByActiveTrue() {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByActiveTrue(Sort.by(Sort.Direction.DESC, "id"));
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toAllResponse(transactionHistories));
    }

    @Override
    public ApiResponse update(TransactionHistoryRequest request) {
        TransactionHistory oldTransaction = getTransactionHistory(request.getId());
        TransactionHistory newTransactionHistory = TransactionHistory.toEntity(request);
        newTransactionHistory.setId(request.getId());
        setTransactionHistory(request, newTransactionHistory);

        rollBackTransaction(oldTransaction);
        transactionWithBalance(newTransactionHistory);
        transactionHistoryRepository.save(newTransactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(newTransactionHistory));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        transactionHistory.setActive(false);
        rollBackTransaction(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
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
        transactionHistory.setMainBalance(mainBalanceRepository.findByIdAndActiveTrue(request.getMainBalanceId()).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND)));
        transactionHistory.setBranch(branchRepository.findByIdAndDeleteFalse(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        transactionHistory.setTaker(request.getPhoneNumber() == null ? null : userRepository.findByPhoneNumberAndBlockedFalse(request.getPhoneNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
        transactionHistory.setStudent(request.getAccountNumber() == null ? null : studentRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND)));
    }

    private TransactionHistory getTransactionHistory(Integer integer) {
        return transactionHistoryRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TRANSACTION_HISTORY_NOT_FOUND));
    }
}
