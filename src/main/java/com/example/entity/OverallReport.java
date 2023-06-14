package com.example.entity;

import com.example.enums.Position;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OverallReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Position position;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    private String classLeadership;

    private double fix;

    private double remain;

    private double cashAdvance;

    @ManyToMany
    private List<TeachingHours> teachingHours;
}