package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String reason;

    private Integer days;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Attachment image;

    @ManyToOne
    private Student student;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createDate;

    private boolean active;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;
}
