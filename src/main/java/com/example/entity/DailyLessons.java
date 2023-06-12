package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DailyLessons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int lessonTime;

    private LocalDate day;

    @ManyToOne
    private TypeOfWork typeOfWork;

    @ManyToOne
    private User teacher;

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private StudentClass studentClass;
}
