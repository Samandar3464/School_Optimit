package com.example.entity;

import com.example.model.request.TeachingHoursRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TeachingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private TypeOfWork typeOfWork;

    private int lessonHours;

    private LocalDate date;

    private Integer teacherId;

    public static TeachingHours toTeachingHours(TeachingHoursRequest teachingHoursRequest){
        return TeachingHours
                .builder()
                .lessonHours(teachingHoursRequest.getLessonHours())
                .teacherId(teachingHoursRequest.getTeacherId())
                .build();
    }
}