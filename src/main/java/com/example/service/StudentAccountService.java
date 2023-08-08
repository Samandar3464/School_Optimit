package com.example.service;

import com.example.entity.StudentAccount;
import com.example.entity.TransactionHistory;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentAccountCreate;
import com.example.model.request.StudentAccountRequest;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.StudentAccountResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAccountService implements BaseService<StudentAccountCreate, Integer> {

    private final StudentAccountRepository studentAccountRepository;
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final StudentRepository studentRepository;
    private final MainBalanceRepository mainBalanceRepository;
    private final BranchRepository branchRepository;


    @Override
    public ApiResponse create(StudentAccountCreate request) {
        checkingCreate(request);
        StudentAccount studentAccount = getStudentAccount(request);
        studentAccountRepository.save(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentAccountResponse.toResponse(studentAccount));
    }

    private void checkingCreate(StudentAccountCreate request) {
        if (studentAccountRepository.findByStudentIdAndActiveTrue(request.getStudentId()).isPresent()
                || studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).isPresent()) {
            throw new RecordNotFoundException(Constants.STUDENT_ACCOUNT_ALREADY_EXIST);
        }
    }

    private StudentAccount getStudentAccount(StudentAccountCreate request) {
        StudentAccount studentAccount = new StudentAccount();
        set(request.getAccountNumber(), request.getBranchId(), request.getMainBalanceId(), request.getDiscount(), request.getStudentId(), studentAccount);
        studentAccount.getStudent().setAccountNumber(studentAccount.getAccountNumber());
        studentAccount.setDate(LocalDate.now());
        studentAccount.setActive(true);
        studentAccount.setPaidInFull(false);
        return studentAccount;
    }

    public ApiResponse payment(StudentAccountRequest request) {

        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));

        studentAccount.setBalance(studentAccount.getBalance() + Double.parseDouble(request.getMoney()));

        setPaidInFull(request, studentAccount);

        debtCollectionIfAny(studentAccount);

        createTransactionHistory(request);

        studentAccountRepository.save(studentAccount);

        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentAccountResponse.toResponse(studentAccount));
    }

    private void debtCollectionIfAny(StudentAccount studentAccount) {

        if (studentAccount.isPaidInFull() && studentAccount.getAmountOfDebit() > 0) {

            if (studentAccount.getBalance() >= studentAccount.getAmountOfDebit()) {

                studentAccount.setBalance(studentAccount.getBalance() - studentAccount.getAmountOfDebit());

                studentAccount.setAmountOfDebit(0);

            } else {

                throw new RecordNotFoundException(Constants.STUDENT_BALANCE_NOT_ENOUGH_TO_WITHDRAW_DEBT);

            }

        } else if (studentAccount.getAmountOfDebit() > 0) {

            if (studentAccount.getBalance() >= studentAccount.getAmountOfDebit()) {

                studentAccount.setBalance(studentAccount.getBalance() - studentAccount.getAmountOfDebit());

                studentAccount.setAmountOfDebit(0);

            } else {

                studentAccount.setAmountOfDebit(studentAccount.getAmountOfDebit() - studentAccount.getBalance());

                studentAccount.setBalance(0);

            }

        }
    }

    private void setPaidInFull(StudentAccountRequest request, StudentAccount studentAccount) {

        if (request.isPaidInFull()) {

            if (!studentAccount.isPaidInFull()) {

                studentAccount.setPaidInFull(true);

            } else {

                throw new RecordAlreadyExistException(Constants.STUDENT_ALREADY_PAYED_FOR_YEAR);

            }

        }
    }

    public ApiResponse updatePayment(StudentAccountRequest request) {

        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_ACCOUNT_NOT_FOUND));

        TransactionHistory transactionHistory = transactionHistoryRepository.findFirstByStudentIdAndActiveTrue(studentAccount.getStudent().getId(), Sort.by(Sort.Direction.DESC, "id")).orElseThrow(() -> new RecordNotFoundException(Constants.TRANSACTION_HISTORY_NOT_FOUND));

        setStudentAccountBalance(request.getMoney(), studentAccount, transactionHistory.getMoneyAmount());

        transactionHistoryService.rollBackTransaction(transactionHistory);

        setTransaction(request, transactionHistory);

        transactionHistoryRepository.save(transactionHistory);

//        setStudentAccount(request, studentAccount, transactionHistory);

        studentAccountRepository.save(studentAccount);

        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentAccountResponse.toResponse(studentAccount));
    }

//    private void setStudentAccount(StudentAccountRequest request, StudentAccount studentAccount, TransactionHistory transactionHistory) {
//        studentAccount.setPaidInFull(request.isPaidInFull());
//        studentAccount.setMainBalance(mainBalanceRepository.findByIdAndActiveTrue(request.getMainBalanceId()).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND)));
//        studentAccount.setStudent(transactionHistory.getStudent());
//        studentAccount.setBranch(transactionHistory.getBranch());
//    }

    private void setStudentAccountBalance(String moneyRequest, StudentAccount studentAccount, double moneyAmount) {

        double money = Double.parseDouble(moneyRequest);

        if (money > moneyAmount) {

            studentAccount.setBalance(studentAccount.getBalance() + (money - moneyAmount));

        } else {

            if (studentAccount.getBalance() >= (moneyAmount - money)) {

                if (studentAccount.getBalance() > (moneyAmount - money)) {

                    studentAccount.setBalance(studentAccount.getBalance() - (moneyAmount - money));

                }

            } else {

                throw new RecordNotFoundException(Constants.STUDENT_BALANCE_NOT_ENOUGH);

            }

        }
    }


    private void setTransaction(StudentAccountRequest request, TransactionHistory transactionHistory) {
        transactionHistory.setDate(LocalDateTime.now());
        transactionHistory.setComment(request.getComment());
        transactionHistory.setExpenseType(request.getExpenseType());
        transactionHistory.setPaymentType(request.getPaymentType());
        transactionHistory.setMoneyAmount(Double.parseDouble(request.getMoney()));
        transactionHistory.setBranch(branchRepository.findByIdAndDeleteFalse(request.getBranchId()).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
        transactionHistory.setStudent(studentRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND)));
        transactionHistoryService.transactionWithBalance(transactionHistory);
    }


    @Scheduled(cron = "00 00 00 1 JAN-DEC ?", zone = "Asia/Samarkand")
    public void withdrawMonthlyPayment() {

        List<StudentAccount> all = studentAccountRepository.findAllByActiveTrue(Sort.by(Sort.Direction.DESC, "id"));

        all.forEach(studentAccount -> {

            double paymentAmount = studentAccount.getStudent().getPaymentAmount();

            if (studentAccount.getDiscount() > 0) {

                if (!studentAccount.isPaidInFull()) {

                    double withoutDiscount = paymentAmount - (paymentAmount % studentAccount.getDiscount());

                    if (studentAccount.getBalance() >= withoutDiscount) {

                        studentAccount.setBalance(studentAccount.getBalance() - withoutDiscount);

                    } else {

                        studentAccount.setAmountOfDebit(withoutDiscount - studentAccount.getBalance());

                        studentAccount.setBalance(0);

                    }
                }

            } else {

                if (studentAccount.getBalance() >= paymentAmount) {

                    studentAccount.setBalance(studentAccount.getBalance() - paymentAmount);

                } else {

                    studentAccount.setAmountOfDebit(paymentAmount - studentAccount.getBalance());

                    studentAccount.setBalance(0);

                }

            }
            studentAccountRepository.save(studentAccount);
        });
    }

    private void createTransactionHistory(StudentAccountRequest request) {
        TransactionHistoryRequest transactionHistory = new TransactionHistoryRequest();
        transactionHistory.setAccountNumber(request.getAccountNumber());
        transactionHistory.setComment(request.getComment());
        transactionHistory.setPaymentType(request.getPaymentType());
        transactionHistory.setExpenseType(request.getExpenseType());
        transactionHistory.setMoneyAmount(String.valueOf(request.getMoney()));
        transactionHistory.setMainBalanceId(request.getMainBalanceId());
        transactionHistory.setBranchId(request.getBranchId());
        transactionHistory.setPaidInFull(request.isPaidInFull());
        transactionHistoryService.create(transactionHistory);
    }

    @Override
    public ApiResponse getById(Integer accountNumber) {
        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(accountNumber.toString()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));
        StudentAccountResponse response = StudentAccountResponse.toResponse(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse getByBranchId(Integer branchId) {
        List<StudentAccount> all = studentAccountRepository.findAllByBranch_IdAndActiveTrue(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentAccountResponse> allResponse = StudentAccountResponse.toAllResponse(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    public ApiResponse getAllByDebtActive() {
        List<StudentAccount> all = studentAccountRepository.findAllByActiveTrueAndAmountOfDebitIsNotNull(Sort.by(Sort.Direction.DESC, "id"));
        List<StudentAccountResponse> allResponse = StudentAccountResponse.toAllResponse(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    @Override
    public ApiResponse update(StudentAccountCreate request) {
        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));
        set(Integer.parseInt(request.getNewAccountNumber()) == 0 ? request.getAccountNumber() : request.getNewAccountNumber(), request.getBranchId(), request.getMainBalanceId(), request.getDiscount(), request.getStudentId(), studentAccount);
        studentAccount.getStudent().setAccountNumber(studentAccount.getAccountNumber());
        studentAccountRepository.save(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentAccountResponse.toResponse(studentAccount));
    }

    @Override
    public ApiResponse delete(Integer accountNumber) {
        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(accountNumber.toString()).orElseThrow(() -> new RecordNotFoundException(Constants.SUCCESSFULLY));
        studentAccount.setActive(false);
        studentAccountRepository.save(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, StudentAccountResponse.toResponse(studentAccount));
    }


    private void set(String accountNumber, Integer branchId, Integer mainBalanceId, String discount, Integer studentId, StudentAccount studentAccount) {
        studentAccount.setDiscount(Integer.parseInt(discount));
        studentAccount.setAccountNumber(accountNumber);
        studentAccount.setMainBalance(mainBalanceRepository.findByIdAndActiveTrue(mainBalanceId).orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND)));
        studentAccount.setStudent(studentRepository.findByIdAndActiveTrue(studentId).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND)));
        studentAccount.setBranch(branchRepository.findById(branchId).orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND)));
    }
}