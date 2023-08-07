package com.example.entity;

import com.example.model.request.SubjectRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private boolean active;

    @ManyToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public static Subject toEntity(SubjectRequestDto subjectRequestDto) {
        return Subject
                .builder()
                .name(subjectRequestDto.getName())
                .active(true)
                .build();
    }
}
