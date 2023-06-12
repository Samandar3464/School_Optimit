package com.example.model.request;

import com.example.entity.WorkExperience;
import lombok.*;


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

    public static WorkExperienceDto toWorkExperienceDto(WorkExperience workExperience){
        return WorkExperienceDto
                .builder()
                .id(workExperience.getId())
                .placeOfWork(workExperience.getPlaceOfWork())
                .startDate(workExperience.getStartDate().toString())
                .endDate(workExperience.getEndDate().toString())
                .position(workExperience.getPosition())
                .employeeId(workExperience.getEmployee()).build();
    }
}
