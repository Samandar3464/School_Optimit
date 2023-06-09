package com.example.model.response;

import com.example.entity.*;
import com.example.enums.Gender;
import com.example.service.AttachmentService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Integer id;

    private Long INN;

    private Long INPS;

    private String fullName;

    private String biography;

//    private String fireBaseToken;

    private LocalDateTime registeredDate;

    private String phoneNumber;

    private String profilePhotoUrl;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(cascade = CascadeType.ALL)
    private Attachment profilePhoto;

    @OneToMany
    private List<Achievement> achievements;

    @OneToMany
    private List<Subject> subjects;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Role> roles;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
//                .fireBaseToken(user.getFireBaseToken())
                .registeredDate(user.getRegisteredDate())
                .fullName(user.getFullName())
                .achievements(user.getAchievements())
                .biography(user.getBiography())
                .INN(user.getINN())
                .INPS(user.getINPS())
                .subjects(user.getSubjects())
                .roles(user.getRoles())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate()==null ? null : user.getBirthDate())
                .gender(user.getGender())
                .build();
    }
}
