package com.example.model.request;


import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyLessonsDto {

    private Integer id;

    private double sumSalaryPerDay;

    private int workingHoursPerDay;

    private String workingDay;

    private List<Integer> typeOfWorkIds;

    private Integer teacherId;
}
