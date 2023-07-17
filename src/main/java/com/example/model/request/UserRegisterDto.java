package com.example.model.request;

import com.example.enums.Gender;
import com.example.enums.Position;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String fatherName;

    @NotBlank
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private int inn;

    private int inps;

    private String biography;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    private Gender gender;

    private MultipartFile profilePhoto;

    private Integer roleId;

    private boolean married;

    private Integer branchId;


    private List<Integer> subjectsIds;

    private List<Integer> dailyLessonsIds;


}
