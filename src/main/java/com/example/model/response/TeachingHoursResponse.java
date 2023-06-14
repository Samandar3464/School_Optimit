package com.example.model.response;


import com.example.entity.TeachingHours;
import com.example.entity.TypeOfWork;
import lombok.*;

@Getter
@Setter
@Builder
public class TeachingHoursResponse {

    private Integer id;

    private TypeOfWork typeOfWork;

    private int lessonHours;

    private String date;

    private Integer teacherId;

    public static TeachingHoursResponse teachingHoursDTO(TeachingHours teachingHours){
        return TeachingHoursResponse
                .builder()
                .id(teachingHours.getId())
                .typeOfWork(teachingHours.getTypeOfWork())
                .lessonHours(teachingHours.getLessonHours())
                .date(teachingHours.getDate().toString())
                .teacherId(teachingHours.getTeacherId())
                .build();
    }

}
