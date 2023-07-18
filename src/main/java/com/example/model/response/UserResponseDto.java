package com.example.model.response;

import com.example.entity.*;
import com.example.model.request.AchievementDto;
import com.example.model.request.StudentClassDto;
import com.example.model.request.WorkExperienceDto;
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

    private String name;

    private String surname;

    private String fatherName;

    private String phoneNumber;

    private String emailAddress;

    private Role role;

    private String birthDate;

    private String gender;

    private int inn;

    private int inps;

    private String profilePhoto;

    private String biography;

    private String registeredDate;


    private StudentClassDto studentClass;

    private List<AchievementDto> achievements;

    private List<WorkExperienceDto> workExperiences;

    private List<DailyLessons> dailyLessons;

    private List<Subject> subjects;

    private List<SalaryResponse> salaries;

    private List<TeachingHoursResponse> teachingHoursResponses;


    public static UserResponseDto from(User user, String photoUrl) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .fatherName(user.getFatherName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .birthDate(user.getBirthDate().toString())
                .registeredDate(user.getRegisteredDate().toString())
                .gender(user.getGender().toString())
                .profilePhoto(photoUrl)
                .inn(user.getInn() == 0 ? 0 : user.getInn())
                .inps(user.getInps() == 0 ? 0 : user.getInps())
                .biography(user.getBiography() == null ? null : user.getEmail())
                .emailAddress(user.getEmail() == null ? null : user.getEmail())

                .dailyLessons(user.getDailyLessons())
//                .workExperiences(WorkExperienceDto.toAllResponse(user.getWorkExperiences()))
                .achievements(AchievementDto.toAllResponse(user.getAchievements()))
                .teachingHoursResponses(TeachingHoursResponse.toAllResponse(user.getTeachingHours()))
                .studentClass(StudentClassDto.toResponse(user.getStudentClass()))
                .salaries(SalaryResponse.toAllResponse(user.getSalaries()))
                .subjects(user.getSubjects())
                .build();
    }
}
