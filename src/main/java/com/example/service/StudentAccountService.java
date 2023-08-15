package com.example.service;

import com.example.entity.*;
import com.example.enums.Constants;
import com.example.exception.RecordAlreadyExistException;
import com.example.exception.RecordNotFoundException;
import com.example.model.common.ApiResponse;
import com.example.model.request.StudentAccountCreate;
import com.example.model.request.StudentAccountRequest;
import com.example.model.request.TransactionHistoryRequest;
import com.example.model.response.MainBalanceResponse;
import com.example.model.response.StudentAccountResponse;
import com.example.model.response.StudentResponse;
import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ModelMapper modelMapper;


    @Override
    public ApiResponse create(StudentAccountCreate request) {
        checkingCreate(request);
        StudentAccount studentAccount = getStudentAccount(request);
        studentAccountRepository.save(studentAccount);
        StudentAccountResponse response = getStudentAccountResponse(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    private StudentAccountResponse getStudentAccountResponse(StudentAccount studentAccount) {
        StudentAccountResponse response = modelMapper.map(studentAccount, StudentAccountResponse.class);
        response.setDate(studentAccount.getDate().toString());
        response.setStudent(modelMapper.map(studentAccount.getStudent(), StudentResponse.class));
        response.setMainBalance(modelMapper.map(studentAccount.getMainBalance(), MainBalanceResponse.class));
        return response;
    }

    private void checkingCreate(StudentAccountCreate request) {
        if (studentAccountRepository.findByStudentIdAndActiveTrue(request.getStudentId()).isPresent()
                || studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).isPresent()) {
            throw new RecordNotFoundException(Constants.STUDENT_ACCOUNT_ALREADY_EXIST);
        }
    }

    private StudentAccount getStudentAccount(StudentAccountCreate request) {
        StudentAccount studentAccount = new StudentAccount();
        MainBalance mainBalance = mainBalanceRepository.findByIdAndActiveTrue(request.getMainBalanceId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.MAIN_BALANCE_NOT_FOUND));
        Student student = studentRepository.findByIdAndActiveTrue(request.getStudentId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));

        studentAccount.setActive(true);
        studentAccount.setBranch(branch);
        studentAccount.setPaidInFull(false);
        studentAccount.setStudent(student);
        studentAccount.setDate(LocalDate.now());
        studentAccount.setMainBalance(mainBalance);
        studentAccount.getStudent().setAccountNumber(request.getAccountNumber());
        studentAccount.setAccountNumber(request.getAccountNumber());
        studentAccount.setDiscount(Integer.parseInt(request.getDiscount()));
        studentAccount.getStudent().setAccountNumber(studentAccount.getAccountNumber());
        return studentAccount;
    }

    public ApiResponse payment(StudentAccountRequest request) {

        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber()).orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));

        studentAccount.setBalance(studentAccount.getBalance() + Double.parseDouble(request.getMoney()));

        setPaidInFull(request, studentAccount);

        debtCollectionIfAny(studentAccount);

        createTransactionHistory(request);

        studentAccountRepository.save(studentAccount);

        return new ApiResponse(Constants.SUCCESSFULLY, true, getStudentAccountResponse(studentAccount));
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

        StudentAccount studentAccount = studentAccountRepository
                .findByAccountNumberAndActiveTrue(request.getAccountNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_ACCOUNT_NOT_FOUND));

        TransactionHistory transactionHistory = transactionHistoryRepository
                .findFirstByStudentIdAndActiveTrue(
                        studentAccount.getStudent().getId(),
                        Sort.by(Sort.Direction.DESC, "id"))
                .orElseThrow(() -> new RecordNotFoundException(Constants.TRANSACTION_HISTORY_NOT_FOUND));

        setStudentAccountBalance(request.getMoney(), studentAccount, transactionHistory.getMoneyAmount());

        transactionHistoryService.rollBackTransaction(transactionHistory);
        setTransaction(request, transactionHistory);

        studentAccountRepository.save(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, getStudentAccountResponse(studentAccount));
    }


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
        Branch branch = branchRepository.findByIdAndDeleteFalse(request.getBranchId())
                .orElseThrow(() -> new RecordNotFoundException(Constants.BRANCH_NOT_FOUND));
        Student student = studentRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_NOT_FOUND));

        transactionHistory.setDate(LocalDateTime.now());
        transactionHistory.setComment(request.getComment());
        transactionHistory.setExpenseType(request.getExpenseType());
        transactionHistory.setPaymentType(request.getPaymentType());
        transactionHistory.setMoneyAmount(Double.parseDouble(request.getMoney()));
        transactionHistory.setBranch(branch);
        transactionHistory.setStudent(student);
        transactionHistoryService.transactionWithBalance(transactionHistory);
        transactionHistoryRepository.save(transactionHistory);
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
        StudentAccount studentAccount = studentAccountRepository
                .findByAccountNumberAndActiveTrue(accountNumber.toString())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));
        StudentAccountResponse response = getStudentAccountResponse(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    public ApiResponse getByBranchId(Integer branchId) {
        List<StudentAccount> all = studentAccountRepository
                .findAllByBranch_IdAndActiveTrue(branchId, Sort.by(Sort.Direction.DESC, "id"));
        List<StudentAccountResponse> allResponse = getStudentAccountResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    private List<StudentAccountResponse> getStudentAccountResponses(List<StudentAccount> all) {
        List<StudentAccountResponse> allResponse = new ArrayList<>();
        all.forEach(studentAccount -> {
            allResponse.add(getStudentAccountResponse(studentAccount));
        });
        return allResponse;
    }

    public ApiResponse getAllByDebtActive() {
        List<StudentAccount> all = studentAccountRepository
                .findAllByActiveTrueAndAmountOfDebitIsNotNull(Sort.by(Sort.Direction.DESC, "id"));
        List<StudentAccountResponse> allResponse = getStudentAccountResponses(all);
        return new ApiResponse(Constants.SUCCESSFULLY, true, allResponse);
    }

    @Override
    public ApiResponse update(StudentAccountCreate request) {
        studentAccountRepository.findByAccountNumberAndActiveTrue(request.getAccountNumber())
                .orElseThrow(() -> new RecordNotFoundException(Constants.STUDENT_TRANSACTION_NOT_FOUND));
        StudentAccount studentAccount = getStudentAccount(request);
        studentAccountRepository.save(studentAccount);
        StudentAccountResponse response = getStudentAccountResponse(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }

    @Override
    public ApiResponse delete(Integer accountNumber) {
        StudentAccount studentAccount = studentAccountRepository.findByAccountNumberAndActiveTrue(accountNumber.toString())
                .orElseThrow(() -> new RecordNotFoundException(Constants.SUCCESSFULLY));
        studentAccount.setActive(false);
        studentAccountRepository.save(studentAccount);
        StudentAccountResponse response = getStudentAccountResponse(studentAccount);
        return new ApiResponse(Constants.SUCCESSFULLY, true, response);
    }
}