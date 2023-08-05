package com.example.model.response;

import com.example.entity.Branch;
import com.example.entity.Level;
import com.example.entity.Room;
import com.example.entity.StudentClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentClassResponse {

    private Integer id;

    private Level level;

    private String className;

    private String startDate;

    private String endDate;

    private boolean active;

    private Room room;

    private Branch branch;

    private UserResponseDto classLeader;

    public static StudentClassResponse toResponse(StudentClass studentClass){
        return StudentClassResponse
                .builder()
                .id(studentClass.getId())
                .className(studentClass.getClassName())
                .branch(studentClass.getBranch())
                .classLeader(UserResponseDto.from(studentClass.getClassLeader()))
                .level(studentClass.getLevel())
                .room(studentClass.getRoom())
                .startDate(studentClass.getStartDate().toString())
                .endDate(studentClass.getEndDate().toString())
                .active(studentClass.isActive())
                .build();
    }

    public static List<StudentClassResponse> toAllResponse(List<StudentClass> allByActiveTrue) {
        List<StudentClassResponse> studentClassResponses = new ArrayList<>();
        allByActiveTrue.forEach(studentClass -> {
            studentClassResponses.add(toResponse(studentClass));
        });
        return studentClassResponses;
    }
}
