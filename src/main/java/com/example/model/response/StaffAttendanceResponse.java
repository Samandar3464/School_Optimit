package com.example.model.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StaffAttendanceResponse {

    private Integer id;

    private boolean cameToWork;

    private LocalDate date;

    private String description;

    private UserResponseDto userResponseDto;
}
