package com.example.service;

import com.example.entity.MainBalance;
import com.example.entity.TransactionHistory;
import com.example.enums.Constants;
import com.example.enums.ExpenseType;
import com.example.enums.PaymentType;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.TransactionHistoryResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService implements BaseService<TransactionHistoryRequest, Integer> {

    private final MainBalanceRepository mainBalanceRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public ApiResponse create(TransactionHistoryRequest request) {
        TransactionHistory transactionHistory = TransactionHistory.toEntity(request);
        setTransactionHistory(request, transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
        transactionWithBalance(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
    }

    @Override
    public ApiResponse getById(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
    }

    public ApiResponse findAllByBranch_IdAndActiveTrue(Integer branchId) {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByBranch_IdAndActiveTrue(branchId);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toAllResponse(transactionHistories));
    }

    public ApiResponse findAllByActiveTrue() {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAllByActiveTrue();
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toAllResponse(transactionHistories));
    }

    @Override
    public ApiResponse update(TransactionHistoryRequest request) {
        TransactionHistory oldTransaction = getTransactionHistory(request.getId());
        TransactionHistory newTransactionHistory = TransactionHistory.toEntity(request);
        newTransactionHistory.setId(request.getId());
        setTransactionHistory(request, newTransactionHistory);
        transactionHistoryRepository.save(newTransactionHistory);

        rollBackTransaction(oldTransaction);
        transactionWithBalance(newTransactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(newTransactionHistory));
    }

    @Override
    public ApiResponse delete(Integer integer) {
        TransactionHistory transactionHistory = getTransactionHistory(integer);
        transactionHistory.setActive(false);
        transactionHistoryRepository.save(transactionHistory);
        rollBackTransaction(transactionHistory);
        return new ApiResponse(Constants.SUCCESSFULLY, true, TransactionHistoryResponse.toResponse(transactionHistory));
    }

    private void transactionWithBalance(TransactionHistory transactionHistory) {

        MainBalance mainBalance = getMainBalance(transactionHistory.getMainBalanceId());

        if (transactionHistory.getExpenseType().equals(ExpenseType.SALARY) || transactionHistory.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE)) {

            withdrawMoneyFromTheBalance(transactionHistory, mainBalance);

        } else {

            transferMoneyToTheBalance(transactionHistory, mainBalance);

        }

        mainBalanceRepository.save(mainBalance);
    }

    private void withdrawMoneyFromTheBalance(TransactionHistory transactionHistory, MainBalance mainBalance) {

        if (transactionHistory.getExpenseType().equals(ExpenseType.SALARY) || transactionHistory.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE)) {

            if (mainBalance.getBalance() >= transactionHistory.getMoneyAmount()) {

                if (transactionHistory.getPaymentType().equals(com.example.enums.PaymentType.NAQD)) {

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
    }

    private void transferMoneyToTheBalance(TransactionHistory transactionHistory, MainBalance mainBalance) {

        if (transactionHistory.getExpenseType().equals(ExpenseType.PAYMENT) || transactionHistory.getExpenseType().equals(ExpenseType.ADDITIONAL_PAYMENT)) {

            if (transactionHistory.getPaymentType().equals(com.example.enums.PaymentType.NAQD)) {

                mainBalance.setCashBalance(mainBalance.getCashBalance() + transactionHistory.getMoneyAmount());

            } else {

                mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() + transactionHistory.getMoneyAmount());

            }

            mainBalance.setBalance(mainBalance.getBalance() + transactionHistory.getMoneyAmount());

        }
    }

    private void rollBackTransaction(TransactionHistory oldTransaction) {

        MainBalance mainBalance = getMainBalance(oldTransaction.getMainBalanceId());

        if (oldTransaction.getExpenseType().equals(ExpenseType.SALARY) || oldTransaction.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE)) {

            rollBackMoneyToBalance(oldTransaction, mainBalance);

        } else {

            rollBackMoneyFromBalance(oldTransaction, mainBalance);

        }

        mainBalanceRepository.save(mainBalance);
    }

    private void rollBackMoneyFromBalance(TransactionHistory oldTransaction, MainBalance mainBalance) {

        if (oldTransaction.getExpenseType().equals(ExpenseType.PAYMENT) || oldTransaction.getExpenseType().equals(ExpenseType.ADDITIONAL_PAYMENT)) {

            if (oldTransaction.getPaymentType().equals(PaymentType.NAQD)) {

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

        if (oldTransaction.getExpenseType().equals(ExpenseType.SALARY) || oldTransaction.getExpenseType().equals(ExpenseType.ADDITIONAL_EXPENSE)) {

            if (oldTransaction.getPaymentType().equals(PaymentType.NAQD)) {

                mainBalance.setCashBalance(mainBalance.getCashBalance() + oldTransaction.getMoneyAmount());

            } else {

                mainBalance.setPlasticBalance(mainBalance.getPlasticBalance() + oldTransaction.getMoneyAmount());

            }

            mainBalance.setBalance(mainBalance.getBalance() + oldTransaction.getMoneyAmount());

        }
    }

    private void setTransactionHistory(TransactionHistoryRequest request, TransactionHistory transactionHistory) {
        transactionHistory.setBranch(branchRepository.findById(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        transactionHistory.setTaker(request.getTakerId() == null ? null : userRepository.findById(request.getTakerId()).orElseThrow(() -> new RecordNotFoundException(Constants.USER_NOT_FOUND)));
    }

    private TransactionHistory getTransactionHistory(Integer integer) {
        return transactionHistoryRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.TRANSACTION_HISTORY_NOT_FOUND));
    }

    private MainBalance getMainBalance(Integer integer) {
        return mainBalanceRepository.findByIdAndActiveTrue(integer).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND));
    }
}
