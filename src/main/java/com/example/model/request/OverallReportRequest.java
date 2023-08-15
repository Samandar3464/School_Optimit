package com.example.model.request;

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

    private Integer branchId;
}
