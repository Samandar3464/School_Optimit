package com.example.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class StudentRequest {

    private Integer id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String fatherName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String docNumber;

    @Column(nullable = false)
    private List<MultipartFile> docPhoto;

    private MultipartFile reference;

    @Column(nullable = false)
    private MultipartFile photo;

    private Integer studentClassId;

    private Integer branchId;

    private boolean active;

    private Integer paymentAmount;

    private MultipartFile medDocPhoto;

    @Size(min = 9,max = 9)
    private String phoneNumber;  // phoneNumber

    private String password;

}