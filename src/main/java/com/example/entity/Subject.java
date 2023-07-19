package com.example.entity;

import com.example.model.request.SubjectRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int level;

    private int teachingHours;

    public static Subject from(SubjectRequestDto subjectRequestDto) {
        return Subject
                .builder()
                .name(subjectRequestDto.getName())
                .level(subjectRequestDto.getLevel())
                .teachingHours(subjectRequestDto.getTeachingHours())
                .build();
    }
}
