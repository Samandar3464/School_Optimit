package com.example.entity;

import com.example.model.request.WorkExperienceDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String placeOfWork;

    private String position;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User employee;

    public static WorkExperience toWorkExperience(WorkExperienceDto workExperienceDto,User user) {
        return WorkExperience
                .builder()
                .placeOfWork(workExperienceDto.getPlaceOfWork())
                .position(workExperienceDto.getPosition())
                .startDate(workExperienceDto.getStartDate())
                .endDate(workExperienceDto.getEndDate())
                .employee(user)
                .build();
    }
}
