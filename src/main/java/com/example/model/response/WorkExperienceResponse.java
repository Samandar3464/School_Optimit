package com.example.model.response;

import lombok.Data;


@Data
public class WorkExperienceResponse {

    private Integer id;

    private String placeOfWork;

    private String position;

    private String startDate;

    private String endDate;

    private Integer employeeId;

    private String employeeFullName;
}
