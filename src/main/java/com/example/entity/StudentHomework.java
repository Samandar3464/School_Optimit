package com.example.entity;

import com.example.model.request.StudentHomeworkRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class StudentHomework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int topicNumber;

    private int lessonHour;

    private String homework;

    private String description;

    private LocalDate date;

    private boolean active;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Subject subject;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User teacher;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StudentClass studentClass;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public static StudentHomework toEntity(StudentHomeworkRequest request){
        return StudentHomework
                .builder()
                .homework(request.getHomework())
                .date(request.getDate())
                .description(request.getDescription())
                .lessonHour(request.getLessonHour())
                .topicNumber(request.getTopicNumber())
                .active(true)
                .build();
    }
}
