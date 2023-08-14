package com.example.entity;

import com.example.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private String fullName;

    private String phoneNumber;

    private String password;

    private LocalDateTime registeredDate;

    private String fireBaseToken;

    private Gender gender;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student student;

    private boolean active;

    @ManyToOne
    private Branch branch;


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
