package com.example.model.requestDto;

import com.example.entity.Achievement;
import com.example.entity.Attachment;
import com.example.entity.Subject;
import com.example.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    private Long INN;

    private Long INPS;

    private String biography;

    private LocalDate birthDate;

    private LocalDateTime registeredDate;

    private boolean isBlocked;

    private String fireBaseToken;

    private Integer verificationCode;

    private Gender gender;

    private Attachment profilePhoto;

    private List<Achievement> achievements;

    private List<Subject> subjects;

    private List<RoleRequestDto> requestDtoList;
}
