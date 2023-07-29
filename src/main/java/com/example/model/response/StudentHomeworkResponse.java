package com.example.model.response;

import com.example.entity.StudentHomework;
import com.example.entity.Subject;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class StudentHomeworkResponse {

    private Integer id;

    private int topicNumber;

    private int lessonHour;

    private String homework;

    private String description;

    private String date;

    private Subject subject;

    private Integer teacherId;

    private boolean active;

    public static StudentHomeworkResponse toResponse(StudentHomework studentHomework) {
        return StudentHomeworkResponse
                .builder()
                .id(studentHomework.getId())
                .topicNumber(studentHomework.getTopicNumber())
                .lessonHour(studentHomework.getLessonHour())
                .homework(studentHomework.getHomework())
                .description(studentHomework.getDescription())
                .date(studentHomework.getDate().toString())
                .subject(studentHomework.getSubject())
                .teacherId(studentHomework.getTeacher().getId())
                .active(studentHomework.isActive())
                .build();
    }

    public static List<StudentHomeworkResponse> toAllResponse(List<StudentHomework> all) {
        List<StudentHomeworkResponse> studentHomeworkResponses = new ArrayList<>();
        all.forEach(studentHomework -> {
            studentHomeworkResponses.add(toResponse(studentHomework));
        });
        return studentHomeworkResponses;
    }
}
