package com.example.model.request;

import com.example.entity.Achievement;
import com.example.entity.Attachment;
import com.example.entity.Subject;
import com.example.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class UserRegisterDto {

    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Long inn;

    private Long inps;

    private String biography;

    private String birthDate;

    private String fireBaseToken;

    private Integer verificationCode;

    private Gender gender;

    private MultipartFile profilePhoto;

    private List<Integer> roles;

    private List<Integer> achievements;

    private List<Integer> subjects;

    private List<Integer> dailyLessons;

    private List<Integer> workExperiences;
}
