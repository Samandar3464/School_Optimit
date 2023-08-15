package com.example.model.response;

import com.example.entity.Family;
import lombok.Data;

import java.util.List;

@Data
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
}
