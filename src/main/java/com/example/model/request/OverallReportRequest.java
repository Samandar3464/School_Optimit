package com.example.model.request;

import com.example.enums.Months;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OverallReportRequest {

    private Integer id;

    private LocalDate date;

    private Integer userId;
}
