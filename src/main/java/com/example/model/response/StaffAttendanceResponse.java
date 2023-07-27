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

    private String comeTime;

    private String leaveTime;

    private String description;

    private Integer userId;

    private Integer branchId;

    public static StaffAttendanceResponse toResponse(StaffAttendance staffAttendance) {
        return StaffAttendanceResponse
                .builder()
                .id(staffAttendance.getId())
                .cameToWork(staffAttendance.isCameToWork())
                .comeTime(staffAttendance.getComeTime().toString())
                .leaveTime(staffAttendance.getLeaveTime().toString())
                .date(staffAttendance.getDate().toString())
                .description(staffAttendance.getDescription())
                .userId(staffAttendance.getUser().getId())
                .branchId(staffAttendance.getBranch().getId())
                .build();
    }

    public static List<StaffAttendanceResponse> toAllResponse(List<StaffAttendance> staffAttendances) {
        List<StaffAttendanceResponse> staffAttendanceResponses = new ArrayList<>();
        staffAttendances.forEach(staffAttendance -> {
            staffAttendanceResponses.add(toResponse(staffAttendance));
        });
        return staffAttendanceResponses;
    }
}
