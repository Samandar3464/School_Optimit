package com.example.entity;

import com.example.model.request.StaffAttendanceRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class StaffAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean cameToWork;

    private LocalDateTime comeTime;

    private LocalDateTime leaveTime;

    private LocalDate date;

    private String description;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public static StaffAttendance toStaffAttendance(StaffAttendanceRequest staffAttendanceRequest){
        return StaffAttendance
                .builder()
                .cameToWork(staffAttendanceRequest.isCameToWork())
                .description(staffAttendanceRequest.getDescription())
                .comeTime(staffAttendanceRequest.getComeTime())
                .leaveTime(staffAttendanceRequest.getLeaveTime())
                .date(LocalDate.now())
                .build();
    }
}
