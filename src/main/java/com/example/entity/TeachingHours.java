package com.example.entity;

import com.example.enums.Months;
import com.example.model.request.TeachingHoursRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private Months month;

    private LocalDate date;

    private Integer teacherId;

    @ElementCollection
    private List<Integer> classIds;

    public static TeachingHours toTeachingHours(TeachingHoursRequest teachingHoursRequest){
        return TeachingHours
                .builder()
                .lessonHours(teachingHoursRequest.getLessonHours())
                .teacherId(teachingHoursRequest.getTeacherId())
                .classIds(teachingHoursRequest.getClassIds())
                .build();
    }
}