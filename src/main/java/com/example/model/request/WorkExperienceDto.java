package com.example.model.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceDto {

    private Integer id;

    private String placeOfWork;

    private String position;

    private String startDate;

    private String endDate;

    private Integer employeeId;
}
