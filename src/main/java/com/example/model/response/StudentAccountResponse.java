package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.MainBalance;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentAccountResponse {

    private Integer id;

    private String accountNumber;

    private double amountOfDebit;

    private double balance;

    private String discount;

    private String date;

    private boolean active;

    private boolean paidInFull;

    private MainBalanceResponse mainBalance;

    private StudentResponse student;

    private Branch branch;

    public static StudentAccountResponse toResponse(StudentAccount studentAccount) {
        return StudentAccountResponse
                .builder()
                .id(studentAccount.getId())
                .amountOfDebit(studentAccount.getAmountOfDebit())
                .balance(studentAccount.getBalance())
                .mainBalance(MainBalanceResponse.toResponse(studentAccount.getMainBalance()))
                .branch(studentAccount.getBranch())
                .paidInFull(studentAccount.isPaidInFull())
                .discount(String.valueOf(studentAccount.getDiscount()))
                .date(studentAccount.getDate() == null ? null : studentAccount.getDate().toString())
                .student(StudentResponse.from(studentAccount.getStudent()))
                .accountNumber(studentAccount.getAccountNumber())
                .active(studentAccount.isActive())
                .build();
    }

    public static List<StudentAccountResponse> toAllResponse(List<StudentAccount> all) {
        List<StudentAccountResponse> studentAccountResponses = new ArrayList<>();
        all.forEach(studentAccount -> {
            studentAccountResponses.add(toResponse(studentAccount));
        });
        return studentAccountResponses;
    }
}
