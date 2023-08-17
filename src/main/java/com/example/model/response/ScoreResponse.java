package com.example.model.response;

import com.example.entity.Student;
import com.example.entity.Subject;
import lombok.Data;

@Data
public class ScoreResponse {

    private Integer id;

    private int score;

    private String description;

    private String createdDate;

    private StudentResponse student;

    private UserResponse teacher;

    private Subject subject;

    private JournalResponse journal;
}
