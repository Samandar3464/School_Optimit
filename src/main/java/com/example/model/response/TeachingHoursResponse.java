package com.example.model.response;


import com.example.entity.TypeOfWork;
import lombok.Data;

@Data
public class TeachingHoursResponse {

    private Integer id;

    private TypeOfWork typeOfWork;

    private int lessonHours;

    private boolean active;

    private String date;

    private SubjectResponse subject;

    private UserResponse teacher;

    private StudentClassResponse studentClass;
}
