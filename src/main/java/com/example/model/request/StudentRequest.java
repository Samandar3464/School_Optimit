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

    private String firstName;

    private String lastName;

    private String fatherName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    private String docNumber;

    private List<MultipartFile> docPhoto;

    private MultipartFile reference;

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
