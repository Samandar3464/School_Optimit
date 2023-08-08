package com.example.entity;

import com.example.enums.Months;
import com.example.model.request.TeachingHoursRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private int lessonHours;

    private LocalDate date;

    @ManyToOne
    private TypeOfWork typeOfWork;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subject subject;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User teacher;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudentClass studentClass;

    private boolean active;

    public static TeachingHours toTeachingHours(TeachingHoursRequest teachingHoursRequest){
        return TeachingHours
                .builder()
                .active(true)
                .lessonHours(teachingHoursRequest.getLessonHours())
                .date(teachingHoursRequest.getDate())
                .build();
    }
}