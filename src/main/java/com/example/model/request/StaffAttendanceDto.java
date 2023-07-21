package com.example.model.request;


import com.example.entity.StaffAttendance;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StaffAttendanceDto {

    private Integer id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime comeTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime goTime;

    private String description;

    private Integer userId;

    private Integer branchId;

    public static StaffAttendanceDto from(StaffAttendance staffAttendance){
        return StaffAttendanceDto.builder()
                .id(staffAttendance.getId())
                .comeTime(staffAttendance.getComeTime() == null ? null : staffAttendance.getComeTime())
                .goTime(staffAttendance.getGoTime() == null ? null : staffAttendance.getGoTime())
                .description(staffAttendance.getDescription()== null ? null : staffAttendance.getDescription())
                .userId(staffAttendance.getUser().getId())
                .build();
    }
}
