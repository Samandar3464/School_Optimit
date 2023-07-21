package com.example.entity;

import com.example.model.request.StaffAttendanceDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    private LocalDateTime comeTime;

    private LocalDateTime goTime;

    private String description;

    private LocalDateTime createdDate;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;

    public static StaffAttendance from(StaffAttendanceDto dto, User user, Branch branch) {
        return StaffAttendance
                .builder()
                .comeTime(dto.getComeTime() == null ? null : dto.getComeTime())
                .goTime(dto.getGoTime() == null ? null : dto.getGoTime())
                .description(dto.getDescription()== null ? null : dto.getDescription())
                .user(user)
                .createdDate(LocalDateTime.now())
                .branch(branch)
                .build();
    }
}
