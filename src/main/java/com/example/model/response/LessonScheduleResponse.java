package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.Room;
import com.example.entity.Subject;
import com.example.entity.TypeOfWork;
import lombok.Data;

@Data
public class LessonScheduleResponse {

    private Integer id;

    private Subject subject;

    private UserResponseDto teacher;

    private StudentClassResponse studentClass;

    private int lessonHour;

    private boolean active;

    private String date;

    private Branch branch;

    private Room room;

    private TypeOfWork typeOfWork;
}
