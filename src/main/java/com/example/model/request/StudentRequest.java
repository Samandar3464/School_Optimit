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

<<<<<<< HEAD
    private List<Integer> docPhotoIds;

    private Integer photoId;
=======
    private List<MultipartFile> docPhoto;

    private MultipartFile reference;

    private MultipartFile photo;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    private Integer studentClassId;

    private Integer branchId;

    private Integer paymentAmount;

<<<<<<< HEAD
    private Integer medDocPhotoId;
=======
    private MultipartFile medDocPhoto;
>>>>>>> 67ccb880a99b336fb6ab7fc42bff89f882b33348

    @Size(min = 9, max = 9)
    private String phoneNumber;  // phoneNumber

    @Size(min = 6)
    private String password;

}
