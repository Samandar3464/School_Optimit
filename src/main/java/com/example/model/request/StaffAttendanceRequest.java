package com.example.model.request;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StaffAttendanceRequest {

    private Integer id;

    private boolean cameToWork;

    private String date;

    private String description;

    private Integer userId;
}
