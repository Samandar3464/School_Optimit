package com.example.entity;

import com.example.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String fullName;

    @NotBlank
    @Size(min = 9,max = 9)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6)
    private String password;

    private LocalDateTime registeredDate;

    private String fireBaseToken;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Transient
    private Integer studentId;

    private boolean active;

    public static Family from(Family family){
        return Family.builder()
                .fullName(family.getFullName())
                .phoneNumber(family.getPhoneNumber())
                .password(family.getPassword())
                .registeredDate(LocalDateTime.now())
                .gender(family.getGender())
                .active(true)
                .build();
    }
}
