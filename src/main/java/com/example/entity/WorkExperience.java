package com.example.entity;

import com.example.model.request.WorkExperienceDto;
import jakarta.persistence.*;
import lombok.*;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @OneToOne
    private User employee;

    public static WorkExperience toWorkExperience(WorkExperienceDto workExperienceDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startDate = LocalDateTime.parse(workExperienceDto.getStartDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(workExperienceDto.getEndDate(), formatter);
        return WorkExperience
                .builder()
                .placeOfWork(workExperienceDto.getPlaceOfWork())
                .position(workExperienceDto.getPosition())
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .build();
    }
}
