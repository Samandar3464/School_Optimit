package com.example.model.request;

import com.example.entity.WorkExperience;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceDto {

    private Integer id;

    private String placeOfWork;

    private String position;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    private Integer employeeId;

    public static WorkExperienceDto toWorkExperienceDto(WorkExperience workExperience) {
        return WorkExperienceDto
                .builder()
                .id(workExperience.getId())
                .placeOfWork(workExperience.getPlaceOfWork())
                .startDate(workExperience.getStartDate())
                .endDate(workExperience.getEndDate())
                .position(workExperience.getPosition())
                .employeeId(workExperience.getEmployee().getId()).build();
    }

//    public static List<WorkExperienceDto> toAllResponse(List<WorkExperience> workExperiences) {
//        List<WorkExperienceDto> workExperienceDtoList = new ArrayList<>();
//        workExperiences.forEach(workExperience -> {
//            workExperienceDtoList.add(toWorkExperienceDto(workExperience));
//        });
//        return workExperienceDtoList;
//    }
}
