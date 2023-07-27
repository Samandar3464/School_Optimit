package com.example.model.response;

import com.example.entity.*;
import com.example.model.request.AchievementDto;
import com.example.model.request.StudentClassDto;
import com.example.model.request.WorkExperienceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Integer id;

    private int inn;

    private int inps;

    private String name;

    private String surname;

    private String fatherName;

    private String biography;

    private String registeredDate;

    private String phoneNumber;

    private String profilePhotoUrl;

    private String birthDate;

    private String gender;

    private List<Subject> subjects;

    private List<Role> roles;


    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .registeredDate(user.getRegisteredDate().toString())
                .name(user.getName())
                .surname(user.getSurname())
                .fatherName(user.getFatherName())
                .biography(user.getBiography())
                .inn(user.getInn())
                .inps(user.getInps())
                .subjects(user.getSubjects())
                .roles(user.getRoles())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate().toString())
                .gender(user.getGender().toString())
                .build();
    }
}
