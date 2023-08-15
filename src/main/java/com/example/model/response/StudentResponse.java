package com.example.model.response;

import com.example.entity.Family;
import com.example.entity.Student;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResponse {

    private Integer id;

    private String firstName;

    private String lastName;

    private String fatherName;

    private String phoneNumber;

    private double paymentAmount;

    private String birthDate;

    private String docNumber;

    private List<String> docPhoto;

    private String reference;

    private String photo;

    private StudentClassResponse studentClass;

    private boolean active;

    private List<Family> families;

    private String addedTime;

    private String medDocPhoto;

    public static StudentResponse from(Student student){
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .paymentAmount(student.getPaymentAmount())
                .fatherName(student.getFatherName())
                .phoneNumber(student.getPhoneNumber())
                .birthDate(student.getBirthDate().toString())
                .docNumber(student.getDocNumber())
                .families(student.getFamilies())
                .studentClass(StudentClassResponse.toResponse(student.getStudentClass()))
                .active(student.isActive())
                .addedTime(student.getAddedTime().toString())
                .build();
    }
}
