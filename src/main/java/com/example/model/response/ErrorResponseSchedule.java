package com.example.model.response;

import com.example.model.request.LessonScheduleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseSchedule {

    private LessonScheduleRequest lessonScheduleRequest;

    private String massage;
}
