package com.example.model.response;

import com.example.entity.*;
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


    public static OverallReportResponse toOverallResponse(OverallReport overallReport) {
        return OverallReportResponse
                .builder()
                .id(overallReport.getId())
                .classLeadership(overallReport.getClassLeadership())
                .month(overallReport.getMonth())
                .branch(overallReport.getBranch())
                .teachingHours(overallReport.getTeachingHours())
                .position(overallReport.getPosition())
                .salary(overallReport.getSalary())
                .fullName(overallReport.getUser().getFullName())
                .build();
    }
}
