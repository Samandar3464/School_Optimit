package com.example.model.response;

import com.example.entity.WorkExperience;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceResponse {

    private Integer id;

    private String placeOfWork;

    private String position;

    private String startDate;

    private String endDate;

    private Integer employeeId;

    private String employeeFullName;

    public static WorkExperienceResponse toResponse(WorkExperience workExperience) {
        return WorkExperienceResponse
                .builder()
                .id(workExperience.getId())
                .placeOfWork(workExperience.getPlaceOfWork())
                .startDate(workExperience.getStartDate().toString())
                .endDate(workExperience.getEndDate().toString())
                .position(workExperience.getPosition())
                .employeeId(workExperience.getEmployee().getId())
                .employeeFullName(workExperience.getEmployee().getName()+" "+workExperience.getEmployee().getSurname())
                .build();
    }


    public static List<WorkExperienceResponse> toAllResponse(List<WorkExperience> workExperiences) {
        List<WorkExperienceResponse> workExperienceDtoList = new ArrayList<>();
        workExperiences.forEach(workExperience -> {
            workExperienceDtoList.add(toResponse(workExperience));
        });
        return workExperienceDtoList;
    }
}
