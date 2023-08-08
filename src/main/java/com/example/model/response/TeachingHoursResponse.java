package com.example.model.response;


import com.example.entity.TeachingHours;
import com.example.entity.TypeOfWork;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class TeachingHoursResponse {

    private Integer id;

    private TypeOfWork typeOfWork;

    private int lessonHours;

    private boolean active;

    private String date;

    private Integer subjectId;

    private Integer teacherId;

    private Integer studentClassId;

    public static TeachingHoursResponse toResponse(TeachingHours teachingHours){
        return TeachingHoursResponse
                .builder()
                .id(teachingHours.getId())
                .subjectId(teachingHours.getSubject().getId())
                .typeOfWork(teachingHours.getTypeOfWork())
                .active(teachingHours.isActive())
                .studentClassId(teachingHours.getStudentClass().getId())
                .lessonHours(teachingHours.getLessonHours())
                .date(teachingHours.getDate().toString())
                .teacherId(teachingHours.getTeacher().getId())
                .build();
    }

    public static List<TeachingHoursResponse> toAllResponse(List<TeachingHours> teachingHoursList) {
        List<TeachingHoursResponse> teachingHoursResponses = new ArrayList<>();
        teachingHoursList.forEach(teachingHours1 -> {
            teachingHoursResponses.add(toResponse(teachingHours1));
        });
        return teachingHoursResponses;
    }
}
