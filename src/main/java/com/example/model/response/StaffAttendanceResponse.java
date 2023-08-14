package com.example.model.response;

import com.example.entity.StaffAttendance;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StaffAttendanceResponse {

    private Integer id;

    private boolean cameToWork;

    private String date;

    private String description;

    private Integer userId;

    private Integer branchId;
}
