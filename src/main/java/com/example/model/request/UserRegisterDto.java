package com.example.model.request;

import com.example.entity.Achievement;
import com.example.entity.Attachment;
import com.example.entity.Subject;
import com.example.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private int inn;

    private int inps;

    private String biography;

    private String birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private MultipartFile profilePhoto;

    private List<Integer> roles;

    private List<AchievementDto> achievements;

    private List<Integer> subjects;

    private List<Integer> dailyLessons;

    private List<WorkExperienceDto> workExperiences;
}
