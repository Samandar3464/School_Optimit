package com.example.model.response;

import com.example.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Integer id;

    private int inn;

    private int inps;

    private String fullName;

    private String biography;

    private String registeredDate;

    private String phoneNumber;

    private String profilePhotoUrl;

    private String birthDate;

    private String gender;

    private Attachment profilePhoto;

    private List<Achievement> achievements;

    private List<WorkExperience> workExperiences;

    private List<DailyLessons> dailyLessons;

    private List<Subject> subjects;

    private List<Role> roles;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .registeredDate(user.getRegisteredDate().toString())
                .fullName(user.getFullName())
                .dailyLessons(user.getDailyLessons())
                .workExperiences(user.getWorkExperiences())
                .achievements(user.getAchievements())
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
