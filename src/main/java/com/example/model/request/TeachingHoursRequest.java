package com.example.model.request;


import com.example.entity.TeachingHours;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeachingHoursRequest {

    private Integer id;

    private Integer typeOfWorkId;

    private int lessonHours;

    private String date;

    private Integer teacherId;

}
