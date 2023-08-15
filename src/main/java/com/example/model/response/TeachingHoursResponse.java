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

    private Integer subjectId;

    private Integer teacherId;

    private Integer studentClassId;
}
