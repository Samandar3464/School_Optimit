package com.example.model.request;


import com.example.entity.Branch;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectLevelDto {


    private Integer id;

    private Integer subjectId;

    private Integer levelId;

    private int teachingHour;

    private double priceForPerHour;

    private Integer branchId;
}
