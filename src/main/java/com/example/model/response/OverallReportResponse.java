package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.Salary;
import com.example.entity.TeachingHours;
import com.example.entity.User;
import com.example.enums.Months;
import com.example.enums.Position;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OverallReportResponse {

    private Integer id;

    @Enumerated(EnumType.STRING)
    private Position position;

    @Enumerated(EnumType.STRING)
    private Months month;

    private String classLeadership;

    private Salary salary;

    private String fullName;

    private List<TeachingHours> teachingHours;

    private Branch branch;
}
